package com.CapStone.inu.taxi.domain.room;

import com.CapStone.inu.taxi.domain.room.dto.kakao.*;
import com.CapStone.inu.taxi.domain.waitingmember.WaitingMember;
import com.CapStone.inu.taxi.domain.waitingmember.WaitingMemberRepository;
import com.CapStone.inu.taxi.domain.waitingmemberRoom.WaitingMemberRoom;
import com.CapStone.inu.taxi.domain.waitingmemberRoom.WaitingMemberRoomRepository;
import com.CapStone.inu.taxi.domain.waitingmemberRoom.WaitingMemberRoomService;
import com.CapStone.inu.taxi.global.common.StatusCode;
import com.CapStone.inu.taxi.global.exception.CustomException;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.CapStone.inu.taxi.global.common.StatusCode.ROOM_MEMBER_NOT_EXIST;
import static com.CapStone.inu.taxi.global.common.StatusCode.ROOM_NOT_EXIST;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class RoomService {
    private final WaitingMemberRepository waitingMemberRepository;
    private final RoomRepository roomRepository;
    private final WaitingMemberRoomRepository waitingMemberRoomRepository;
    private final SimpMessagingTemplate template;
    private final WaitingMemberRoomService waitingMemberRoomService;
    private final TaskScheduler taskScheduler; // 비동기 작업을 예약하고 실행하는 데 사용, 직접 설정 시 매개변수 활요범위가 높다.
    // 여러 사용자의 매칭 작업을 관리할 수 있도록 Map을 사용
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private ScheduledFuture<?> scheduledFuture;

    //Key: userId, value: 해당 유저와 매칭에 성공한 상대 유저 목록
    private HashMap<Long, HashSet<Long>> matched_2 = new HashMap<>();
    private HashMap<Long, HashSet<Pair<Long, Long>>> matched_3 = new HashMap<>();
    private HashMap<Long, HashSet<List<Long>>> matched_4 = new HashMap<>();

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    //kakao api요청을 보내서, 택시 경로, 비용, 시간 등을 응답받아 리턴.
    public ResponseEntity<String> getDirection(Map<String, Object> requestPayload) {
        String url = "https://apis-navi.kakaomobility.com/v1/waypoints/directions";

        // 설정할 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        // 요청을 위한 HttpEntity 객체 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);

        // RestTemplate을 사용한 POST 요청 전송
        RestTemplate restTemplate = new RestTemplate();

        // 외부 API로부터 받은 응답 반환
        return restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class
        );
    }

    //kakao api에 택시를 함께 탈 멤버 리스트를 요청 호출 방식에 맞게 작성.
    public Map<String, Object> makePayload(List<WaitingMember> memberList) {

        Map<String, Object> requestPayload = new HashMap<>();
        List<WaitingMember> optimalRoute = new ArrayList<>();

        //1. 4명의 출발지 - 도착지 쌍이 제일 가까운 두 명을 고른다. (출발지쪽 a, 도착지쪽 b)
        //2. a에서 가까운순으로 4명을 방문, b에서 가까운순으로 4명을 방문한다.
        //3. a의 방문순서 역순으로, b의 방문순서 정순으로 택시가 움직인다.

        Double minDistance = Double.MAX_VALUE;

        Pair<WaitingMember, WaitingMember> startEndPair = memberList.stream()
                .flatMap(A -> memberList.stream().map(B -> Pair.of(A, B))) // 모든 A, B 쌍 생성
                .min(Comparator.comparingDouble(pair -> HaversineCalculator(pair.getFirst(), pair.getSecond(), "start_end"))) // 가장 가까운 쌍 찾기
                .orElse(null);

        WaitingMember startMember = startEndPair.getFirst();
        WaitingMember endMember = startEndPair.getSecond();

        //BFS
        List<WaitingMember> visited = new ArrayList<>();
        WaitingMember currentMember = startMember;
        visited.add(currentMember);

        while (visited.size() < memberList.size()) {
            WaitingMember nextMember = null;
            minDistance = Double.MAX_VALUE;

            // 가장 가까운 멤버 찾기
            for (WaitingMember waitingMember : memberList) {
                if (!visited.contains(waitingMember)) {
                    Double distance = HaversineCalculator(currentMember, waitingMember, "start");
                    if (distance < minDistance) {
                        minDistance = distance;
                        nextMember = waitingMember;
                    }
                }
            }

            // 찾은 멤버를 방문 리스트에 추가하고, 현재 멤버로 설정
            visited.add(nextMember);
            currentMember = nextMember;
        }

        Collections.reverse(visited);
        optimalRoute.addAll(visited);

        visited.clear();
        currentMember = endMember;
        visited.add(currentMember);

        while (visited.size() < memberList.size()) {
            WaitingMember nextMember = null;
            minDistance = Double.MAX_VALUE;

            // 가장 가까운 멤버 찾기
            for (WaitingMember waitingMember : memberList) {
                if (!visited.contains(waitingMember)) {
                    Double distance = HaversineCalculator(currentMember, waitingMember, "end");
                    if (distance < minDistance) {
                        minDistance = distance;
                        nextMember = waitingMember;
                    }
                }
            }

            // 찾은 멤버를 방문 리스트에 추가하고, 현재 멤버로 설정
            visited.add(nextMember);
            currentMember = nextMember;
        }

        optimalRoute.addAll(visited);

        System.out.println(optimalRoute);

        List<Map<String, Double>> waypoints = new ArrayList<>();

        for (WaitingMember waitingMember : visited) {
            Map<String, Double> waypoint = new HashMap<>();
            waypoint.put("x", waitingMember.getStartX());
            waypoint.put("y", waitingMember.getStartY());
            waypoints.add(waypoint);
        }

        Map<String, Double> destination = waypoints.get(waypoints.size() - 1);
        waypoints.remove(waypoints.size() - 1);
        Map<String, Double> origin = waypoints.get(0);
        waypoints.remove(0);


        requestPayload.put("origin", origin);
        requestPayload.put("destination", destination);
        requestPayload.put("waypoints", waypoints);

        // 추가 옵션들 설정
        requestPayload.put("priority", "RECOMMEND");// 경로 탐색 우선순위: RECOMMEND, TIME, DISTANCE 중 선택 가능
        requestPayload.put("avoid", List.of("ferries", "motorway"));// 경로 탐색 제한 옵션, 예시로 페리와 자동차 전용 도로 회피
        requestPayload.put("roadevent", 0);// 유고 정보 반영 옵션
        requestPayload.put("alternatives", false);// 대안 경로 제공 여부
        requestPayload.put("road_details", false);// 상세 도로 정보 제공 여부
        requestPayload.put("car_type", 1);// 차종: 기본값 1
        requestPayload.put("car_fuel", "GASOLINE");// 차량 유종: GASOLINE, DIESEL, LPG 중 선택 가능
        requestPayload.put("car_hipass", false);// 하이패스 장착 여부
        requestPayload.put("summary", false);// 요약 정보 제공 여부

        return requestPayload;
    }

    //A와 B의 위도, 경도 기반으로 실제 거리 계산
    public Double HaversineCalculator(WaitingMember A, WaitingMember B, String command) {
        Double EARTH_RADIUS = 6378.135;//단위: km

        Double A_latitude, A_longitude, B_latitude, B_longitude;
        if (command.equals("start")) {
            A_latitude = A.getStartY();
            A_longitude = A.getStartX();
            B_latitude = B.getStartY();
            B_longitude = B.getStartX();
        } else if (command.equals("end")) {
            A_latitude = A.getEndY();
            A_longitude = A.getEndX();
            B_latitude = B.getEndY();
            B_longitude = B.getEndX();
        } else { //if (command.equals("start_end")) {
            A_latitude = A.getStartY();
            A_longitude = A.getStartX();
            B_latitude = B.getEndY();
            B_longitude = B.getEndX();
        }

        //위도, 경도 라디안으로 변환
        Double latitude_diff = Math.toRadians(B_latitude - A_latitude);//위도 차이
        Double longitude_diff = Math.toRadians(B_longitude - A_longitude);//경도 차이
        Double A_latitude_radian = Math.toRadians(A_latitude);
        Double B_latitude_radian = Math.toRadians(B_latitude);

        //Haversine 공식 계산
        Double a = Math.sin(latitude_diff / 2) * Math.sin(latitude_diff / 2) +
                Math.cos(A_latitude_radian) * Math.cos(B_latitude_radian) *
                        Math.sin(longitude_diff / 2) * Math.sin(longitude_diff / 2);

        Double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return EARTH_RADIUS * c;
    }

    public Integer durationCalculator(Long userId, List<Section> sections) {
        Integer totalDuration = 0;

        WaitingMember waitingMember = waitingMemberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(StatusCode.MEMBER_NOT_EXIST));

        Double endX = waitingMember.getEndX();
        Double endY = waitingMember.getEndY();

        // 각 섹션을 순회하면서 목적지에 도달하기 전까지의 duration을 누적
        for (Section section : sections) {
            for (Road road : section.getRoads()) {
                Double[] vertexes = road.getVertexes();
                // vertexes 배열을 순회하면서 목적지 좌표와 일치하는지 확인
                for (int i = 0; i + 1 < vertexes.length; i += 2) {
                    Double vertexX = vertexes[i];
                    Double vertexY = vertexes[i + 1];

                    // 해당 좌표가 유저의 목적지(endX, endY)와 일치하는지 확인
                    if (vertexX.equals(endX) && vertexY.equals(endY)) {
                        // 현재 섹션의 duration을 포함한 총 시간을 반환
                        totalDuration += section.getDuration();
                        return totalDuration;
                    }
                }
            }
            // 현재 섹션의 duration을 누적
            totalDuration += section.getDuration();
        }

        // 모든 섹션을 다 지나도 목적지에 도달하지 못한 경우 -1 반환 (에러 처리)
        return -1;
    }


    //매칭이 성공한 시점에 방 생성, 생성된 정보를 프론트에 넘겨줌.
    public void makeRoom(ResponseEntity<String> responseEntity, List<WaitingMember> memberList) {
        log.info("방생성 로직 시작");

        Long roomId = 0L;
        List<Long> list = new ArrayList<>();
        for (WaitingMember waitingMember : memberList) {
            list.add(waitingMember.getId());
        }
        Collections.sort(list);
        for (Long id : list) {
            roomId = roomId * 100000L + id;
        }

        Gson gson = new Gson();
        Route route = gson.fromJson(responseEntity.getBody(), ApiResponse.class).getRoutes()[0];//1가지 경로만 탐색함.(getRoutes()[0])

        List<pathInfo> pathInfoList = new ArrayList<>();

        for (Section section : route.getSections()) {
            for (Road road : section.getRoads()) {
                Double[] vertexes = road.getVertexes();
                for (int i = 0; i + 1 < vertexes.length; i += 2) {
                    double x = vertexes[i], y = vertexes[i + 1];
                    pathInfo waypoint = new pathInfo();
                    waypoint.setX(x);
                    waypoint.setY(y);
                    pathInfoList.add(waypoint);
                }
            }
        }

        Integer fare = route.getSummary().getFare().getTaxi() + route.getSummary().getFare().getToll();
        Integer duration = route.getSummary().getDuration();

        Room room = Room.builder()
                .roomId(roomId)
                .taxiFare(fare)
                .taxiDuration(duration)
                .taxiPath(gson.toJson(pathInfoList))
                .taxiHeadcount(memberList.size())
                .driverId(null)
                .build();

        //만약 roomId가 같은 방이 이미 존재했다면, save()함수는 자동으로 update의 역할을 한다.
        //경로가 실시간으로 바뀔 가능성도 있으므로 hashmap 자료구조에 저장되는 최초 1회는 update 되는 것도 좋을듯.
        roomRepository.save(room);

        //waitingmemberroom 만들기. memberList사람수만큼.
        for (WaitingMember waitingMember : memberList) {
            //금액 -> 1/n
            Integer charge = (room.getTaxiFare() + memberList.size() - 1) / memberList.size();
            //소요 시간 -> responseEntity를 보고 알 수 있음.
            Integer time = durationCalculator(waitingMember.getId(), Arrays.stream(route.getSections()).toList());

            System.out.println("[member " + waitingMember.getId() + "] time: " + time + ", charge: " + charge);

            waitingMemberRoomService.makeWaitingMemberRoom(waitingMember, room, time, charge);
        }

        log.info("방 생성 완료");
    }

    public void startMatchAlgorithm(Long userId) {
        stopMatchAlgorithm(userId); // 기존 매칭 작업 중지

        // 새로운 매칭 작업을 시작합니다.
        scheduledFuture = taskScheduler.scheduleWithFixedDelay(() -> matchUser(userId), 5000); // 5초마다

        // 해당 사용자의 매칭 작업을 저장합니다.
        scheduledTasks.put(userId, scheduledFuture);
    }

    /*유저 매칭 시도.
    1. 매칭 시도 버튼을 누른 시점을 기준으로 탐색 범위를 넓혀나가면서, 서로 탐색 범위에 들어온 두 유저는 매칭에 성공함.
    2. A-B가 매칭에 성공하면 방을 생성하고, A-B-C 매칭도 성공했는지 추가로 확인.
    3. 마찬가지로 A-B-C가 매칭에 성공하면 A-B-C-D가 매칭에 성공했는지 추가로 확인.
    * */
    public void matchUser(Long userId) {
        log.info("매치 유저 실행");
        if (!waitingMemberRepository.existsById(userId))
            stopMatchAlgorithm(userId);
        List<WaitingMember> waitingMembers = waitingMemberRepository.findAll();

        tryMatching(userId, waitingMembers);

//         //테스트용.
//         int n = 1;
//         while (n > 0) {
//             if (!waitingMemberRepository.existsById(userId))
//                 break;
//             List<WaitingMember> waitingMembers = waitingMemberRepository.findAll();
//             for (Long i = 1L; i <= 4L; i++)
//                 tryMatching(i, waitingMembers);
//             n--;
//         }
    }

    private void tryMatching(Long userId, List<WaitingMember> waitingMembers) {

        int n = waitingMembers.size();
        WaitingMember A = waitingMemberRepository.findById(userId).orElseThrow(() -> new CustomException(StatusCode.MEMBER_NOT_EXIST));

        for (int j = 0; j < n; j++) {
            WaitingMember B = waitingMembers.get(j);

            //test_print(A, B, A_range, B_range, start_distance, end_distance, now);

            //a-b 매칭 성공
            if (isMatched2(A, B)) {

                List<WaitingMember> memberList = new ArrayList<>(Arrays.asList(A, B));
                ResponseEntity<String> responseEntity = getDirection(makePayload(memberList));
                makeRoom(responseEntity, memberList);

                //if (!matched_2.containsKey(A_Id)) matched_2.put(A_Id, new HashSet<>());
                matched_2.get(A.getId()).add(B.getId());

                if (!matched_2.containsKey(B.getId())) matched_2.put(B.getId(), new HashSet<>());
                matched_2.get(B.getId()).add(A.getId());

                isMatched3(A, B);
            }

            template.convertAndSend("/sub/member/" + userId, waitingMemberRoomService.makeAllRoomResList(userId));
        }
    }

    private boolean isMatched2(WaitingMember A, WaitingMember B) {
        Long A_Id = A.getId(), B_Id = B.getId();
        if (A_Id.compareTo(B_Id) == 0) return false;

//        Long roomId = Math.min(A_Id, B_Id) * 100000L + Math.max(A_Id, B_Id);
//        //이미 존재하는 방이므로, 또 만들 필요 없음. -> 이거 검사 안하는게 낫겠다. 하면, 코드 재시작할때 3~4인 매칭이 제대로 동작하지 않는다.
//        if (roomRepository.findById(roomId).isPresent()) return false;

        if (!matched_2.containsKey(A_Id)) matched_2.put(A_Id, new HashSet<>());
        //이미 a-b가 매칭되어있으므로, 또 검사할 필요 없음.
        if (matched_2.get(A_Id).contains(B_Id)) return false;

        double start_distance = HaversineCalculator(A, B, "start");
        double end_distance = HaversineCalculator(A, B, "end");

        //초당 weight m씩 반경을 넓혀가며 상대방을 찾음.
        LocalDateTime now = LocalDateTime.now();
        double weight = (double) 4.5 / (double) 1000;
        double A_range = Duration.between(A.getCreatedDate(), now).getSeconds() * weight;
        double B_range = Duration.between(B.getCreatedDate(), now).getSeconds() * weight;

        return A_range >= start_distance && B_range >= start_distance
                && A_range >= end_distance && B_range >= end_distance;
    }

    private void isMatched3(WaitingMember A, WaitingMember B) {
        //(a<->b), (b<->c), (c<->a)인 경우 확인.
        for (Long C_Id : matched_2.get(A.getId())) {
            //if (!matched_2.containsKey(B_Id)) matched_2.put(B_Id, new HashSet<>());
            if (!matched_2.get(B.getId()).contains(C_Id)) continue;

            Pair<Long, Long> bc = Pair.of(Math.min(B.getId(), C_Id), Math.max(B.getId(), C_Id));
            if (!matched_3.containsKey(A.getId())) matched_3.put(A.getId(), new HashSet<>());

            //이미 a-b-c는 매칭되어있었음. todo: a-b가 이번에 처음 매칭됐기 때문에 이 코드는 필요없는것 같다. 검토 필요
            //if (matched_3.get(A_Id).contains(bc)) continue;

            //a-b-c 매칭 성공
            WaitingMember C = waitingMemberRepository.findById(C_Id).orElseThrow(() -> new CustomException(StatusCode.MEMBER_NOT_EXIST));
            List<WaitingMember> memberList = new ArrayList<>(Arrays.asList(A, B, C));
            ResponseEntity<String> responseEntity = getDirection(makePayload(memberList));
            makeRoom(responseEntity, memberList);

            //if (!matched_3.containsKey(A_Id)) matched_3.put(A_Id, new HashSet<>());
            matched_3.get(A.getId()).add(bc);

            Pair<Long, Long> ac = Pair.of(Math.min(A.getId(), C_Id), Math.max(A.getId(), C_Id));
            if (!matched_3.containsKey(B.getId())) matched_3.put(B.getId(), new HashSet<>());
            matched_3.get(B.getId()).add(ac);

            Pair<Long, Long> ab = Pair.of(Math.min(A.getId(), B.getId()), Math.max(A.getId(), B.getId()));
            if (!matched_3.containsKey(C_Id)) matched_3.put(C_Id, new HashSet<>());
            matched_3.get(C_Id).add(ab);

            isMatched4(A, B, C, memberList);
        }
    }

    private void isMatched4(WaitingMember A, WaitingMember B, WaitingMember C, List<WaitingMember> memberList) {
        //(a<->b), (a<->c), (a<->d), (b<->c), (b<->d), (c<->d)인 경우 확인.
        //a,b,c는 이미 한 묶음이므로, (a<->d), (b<->d), (c<->d)만 확인하면 된다. 이건 (a<->cd), (b<->cd)만 확인하면 된다.
        for (Pair<Long, Long> cd : matched_3.get(A.getId())) {
            //if (!matched_3.containsKey(B_Id)) matched_3.put(B_Id, new HashSet<>());
            if (!matched_3.get(B.getId()).contains(cd)) continue;
            Long D_Id;
            if (!cd.getFirst().equals(C.getId())) D_Id = cd.getFirst();
            else D_Id = cd.getSecond();

            List<Long> bcd = new ArrayList<>(List.of(B.getId(), C.getId(), D_Id));
            Collections.sort(bcd);
            if (!matched_4.containsKey(A.getId())) matched_4.put(A.getId(), new HashSet<>());
            //이미 a-b-c-d는 매칭되어있었음.todo: a-b가 이번에 처음 매칭됐기 때문에 이 코드도 필요없는것 같다. 검토 필요
            //if (matched_4.get(A_Id).contains(bcd)) continue;

            //a-b-c-d 매칭 성공
            WaitingMember D = waitingMemberRepository.findById(D_Id).orElseThrow(() -> new CustomException(StatusCode.MEMBER_NOT_EXIST));
            while (memberList.size() > 3) memberList.remove(memberList.size() - 1);
            memberList.add(D);
            ResponseEntity<String> responseEntity = getDirection(makePayload(memberList));
            makeRoom(responseEntity, memberList);

            //if (!matched_4.containsKey(A_Id)) matched_4.put(A_Id, new HashSet<>());
            matched_4.get(A.getId()).add(bcd);

            List<Long> acd = new ArrayList<>(List.of(A.getId(), C.getId(), D_Id));
            Collections.sort(acd);
            if (!matched_4.containsKey(B.getId())) matched_4.put(B.getId(), new HashSet<>());
            matched_4.get(B.getId()).add(acd);

            List<Long> abd = new ArrayList<>(List.of(A.getId(), B.getId(), D_Id));
            Collections.sort(abd);
            if (!matched_4.containsKey(C.getId())) matched_4.put(C.getId(), new HashSet<>());
            matched_4.get(C.getId()).add(abd);

            List<Long> abc = new ArrayList<>(List.of(A.getId(), B.getId(), C.getId()));
            Collections.sort(abc);
            if (!matched_4.containsKey(D_Id)) matched_4.put(D_Id, new HashSet<>());
            matched_4.get(D_Id).add(abc);
        }
    }


    public void cancelMatching(Long userId) {

        stopMatchAlgorithm(userId);

        List<WaitingMemberRoom> waitingMemberRoomList = waitingMemberRoomRepository.findByWaitingMember_Id(userId);
        for (WaitingMemberRoom waitingMemberRoom : waitingMemberRoomList) {
            roomRepository.deleteById(waitingMemberRoom.getRoom().getRoomId());
        }

        matched_2.forEach((key, value) -> value.removeIf(userId::equals));
        matched_3.forEach((key, value) -> value.removeIf(userIds ->
                userId.equals(userIds.getFirst()) || userId.equals(userIds.getSecond())));
        matched_4.forEach((key, value) -> value.removeIf(userIds -> userIds.stream().anyMatch(userId::equals)));
    }

    public void stopMatchAlgorithm(Long userId) {
        ScheduledFuture<?> scheduledFuture = scheduledTasks.get(userId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false); // 스케줄러 중지
            scheduledTasks.remove(userId); // 해당 사용자의 매칭 작업을 Map에서 제거
        }
    }

    public void ready(Long roomId, Long userId) {
        WaitingMemberRoom waitingMemberRoom = waitingMemberRoomRepository.findByRoom_RoomIdAndWaitingMember_Id(roomId, userId)
                .orElseThrow(() -> new CustomException(ROOM_MEMBER_NOT_EXIST));
        waitingMemberRoom.updateReady();
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new CustomException(ROOM_NOT_EXIST));

        // 방에있는 모든 유저가 레디했는지?
        boolean allReady = true;
        //ID가 roomId인 모든 WaitingMemberRoom 조회.
        List<WaitingMemberRoom> waitingMemberRoomList = waitingMemberRoomRepository.findByRoom_RoomId(roomId);
        for (WaitingMemberRoom WMR : waitingMemberRoomList) {
            //roomId가 속한 모든 user 에 대해,
            //원래 roomRes였지만 전체 방을 보내야할 듯
            template.convertAndSend("/sub/member/" + WMR.getWaitingMember().getId(), waitingMemberRoomService.makeAllRoomResList(userId));
            if (!WMR.getIsReady()) allReady = false;
        }

        if (allReady) {
            room.setIsStart();
            for (WaitingMemberRoom WMR : waitingMemberRoomList){
                startMatchAlgorithm(WMR.getWaitingMember().getId());
                template.convertAndSend("/sub/member/" + WMR.getWaitingMember().getId(), waitingMemberRoomService.makeRoomRes(room,WMR.getWaitingMember().getId()));
            }
            log.info("모두 준비 완료");
        }
    }
}