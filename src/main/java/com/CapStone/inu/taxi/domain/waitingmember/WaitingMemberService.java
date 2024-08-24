package com.CapStone.inu.taxi.domain.waitingmember;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WaitingMemberService {
    private final WaitingMemberRepository waitingMemberRepository;

    private HashMap<Long, HashSet<Long>> matched_2 = new HashMap<>();
    private HashMap<Long, HashSet<Pair<Long, Long>>> matched_3 = new HashMap<>();
    private HashMap<Long, HashSet<List<Long>>> matched_4 = new HashMap<>();

    public void addUser() {}

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

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
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, String.class
        );

        System.out.println(response);
        return response;
    }

    public Map<String, Object> makePayload(List<WaitingMember> memberList) {
        //일단 아무 기사님이나 데려왔고, 택시가 임의의 순서로 멤버에게 간다고 치자.
        //todo: 나중에 각 멤버 중 가장 가까이 있는 기사님으로 바꿔야 하고, 누구부터 데려가서 누구부터 내려줄지 정해야함.
        Double driver_x = 127.11023403583478, driver_y = 37.39434769502827;

        Map<String, Object> requestPayload = new HashMap<>();

        Map<String, Double> origin = new HashMap<>();
        origin.put("x", driver_x); // 출발지 경도
        origin.put("y", driver_y); // 출발지 위도

        int n = memberList.size();

        List<Map<String, Double>> waypoints = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Map<String, Double> waypoint = new HashMap<>();
            waypoint.put("x", memberList.get(i).getStartX());
            waypoint.put("y", memberList.get(i).getStartY());
            waypoints.add(waypoint);
        }
        for (int i = 0; i < n - 1; i++) {
            Map<String, Double> waypoint = new HashMap<>();
            waypoint.put("x", memberList.get(i).getEndX());
            waypoint.put("y", memberList.get(i).getEndY());
            waypoints.add(waypoint);
        }

        Map<String, Double> destination = new HashMap<>();
        destination.put("x", memberList.get(n - 1).getEndX());
        destination.put("y", memberList.get(n - 1).getEndY());

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
        } else {// if (command.equals("end"))
            A_latitude = A.getEndY();
            A_longitude = A.getEndX();
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

    public void test_print(WaitingMember A, WaitingMember B, double A_range, double B_range,
                           double start_distance, double end_distance, LocalDateTime now) {
        System.out.println("A: " + A.getUserId() + " / " + A.getStartX() + "," + A.getStartY() +
                " / " + A.getEndX() + "," + A.getEndY() + " / " + A.getCreatedDate());
        System.out.println("B: " + B.getUserId() + " / " + B.getStartX() + "," + B.getStartY() +
                " / " + B.getEndX() + "," + B.getEndY() + " / " + B.getCreatedDate());
        System.out.println("A_range: " + A_range);
        System.out.println("B_range: " + B_range);
        System.out.println("start_diatance: " + start_distance);
        System.out.println("end_distance: " + end_distance);
        System.out.println("now: " + now);
        System.out.println();
    }

    public void matchUser() {
        List<WaitingMember> waitingMembers = waitingMemberRepository.findAll();

        /*.add() 함수 호출하는 부분마다, 프론트한테 매칭됐다고 알려주고, db에 넣어야함.
        그리고, n명이 매칭되면 n-1명인 매칭들은 지우라고 알려줘야함.
        * */

        int n = waitingMembers.size();

        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                WaitingMember A = waitingMembers.get(i), B = waitingMembers.get(j);
                Long A_Id = A.getUserId(), B_Id = B.getUserId();

                if (!matched_2.containsKey(A_Id)) matched_2.put(A_Id, new HashSet<>());
                //이미 a-b가 매칭되어있으므로, 또 검사할 필요 없음.
                if (matched_2.get(A_Id).contains(B_Id)) continue;

                double start_distance = HaversineCalculator(A, B, "start");
                double end_distance = HaversineCalculator(A, B, "end");

                //초당 weight m씩 반경을 넓혀가며 상대방을 찾음.
                LocalDateTime now = LocalDateTime.now();
                double weight = (double) 0.003 / (double) 1000;
                double A_range = Duration.between(waitingMembers.get(i).getCreatedDate(), now).getSeconds() * weight;
                double B_range = Duration.between(waitingMembers.get(j).getCreatedDate(), now).getSeconds() * weight;

                boolean match_success = A_range >= start_distance && B_range >= start_distance
                        && A_range >= end_distance && B_range >= end_distance;

                //test_print(A, B, A_range, B_range, start_distance, end_distance, now);

                //a-b 매칭 성공
                if (match_success) {
                    List<WaitingMember> memberList = new ArrayList<>();
                    memberList.add(A);
                    memberList.add(B);
                    getDirection(makePayload(memberList));

                    //if (!matched_2.containsKey(A_Id)) matched_2.put(A_Id, new HashSet<>());
                    matched_2.get(A_Id).add(B_Id);

                    if (!matched_2.containsKey(B_Id)) matched_2.put(B_Id, new HashSet<>());
                    matched_2.get(B_Id).add(A_Id);

                    //(a<->b), (b<->c), (c<->a)인 경우 확인.
                    for (Long C_Id : matched_2.get(A_Id)) {
                        //if (!matched_2.containsKey(B_Id)) matched_2.put(B_Id, new HashSet<>());
                        if (!matched_2.get(B_Id).contains(C_Id)) continue;

                        Pair<Long, Long> bc = Pair.of(Math.min(B_Id, C_Id), Math.max(B_Id, C_Id));
                        if (!matched_3.containsKey(A_Id)) matched_3.put(A_Id, new HashSet<>());

                        //이미 a-b-c는 매칭되어있었음. todo: a-b가 이번에 처음 매칭됐기 때문에 이 코드는 필요없는것 같다. 검토 필요
                        //if (matched_3.get(A_Id).contains(bc)) continue;

                        //a-b-c 매칭 성공
                        Optional<WaitingMember> C = waitingMemberRepository.findById(C_Id);
                        while (memberList.size() > 2) memberList.remove(memberList.size() - 1);
                        memberList.add(C.get());
                        getDirection(makePayload(memberList));

                        //if (!matched_3.containsKey(A_Id)) matched_3.put(A_Id, new HashSet<>());
                        matched_3.get(A_Id).add(bc);

                        Pair<Long, Long> ac = Pair.of(Math.min(A_Id, C_Id), Math.max(A_Id, C_Id));
                        if (!matched_3.containsKey(B_Id)) matched_3.put(B_Id, new HashSet<>());
                        matched_3.get(B_Id).add(ac);

                        Pair<Long, Long> ab = Pair.of(Math.min(A_Id, B_Id), Math.max(A_Id, B_Id));
                        if (!matched_3.containsKey(C_Id)) matched_3.put(C_Id, new HashSet<>());
                        matched_3.get(C_Id).add(ab);

                        //(a<->b), (a<->c), (a<->d), (b<->c), (b<->d), (c<->d)인 경우 확인.
                        //a,b,c는 이미 한 묶음이므로, (a<->d), (b<->d), (c<->d)만 확인하면 된다. 이건 (a<->cd), (b<->cd)만 확인하면 된다.
                        for (Pair<Long, Long> cd : matched_3.get(A_Id)) {
                            //if (!matched_3.containsKey(B_Id)) matched_3.put(B_Id, new HashSet<>());
                            if (!matched_3.get(B_Id).contains(cd)) continue;
                            Long D_Id;
                            if (!cd.getFirst().equals(C_Id)) D_Id = cd.getFirst();
                            else D_Id = cd.getSecond();

                            List<Long> bcd = new ArrayList<>(List.of(B_Id, C_Id, D_Id));
                            Collections.sort(bcd);
                            if (!matched_4.containsKey(A_Id)) matched_4.put(A_Id, new HashSet<>());
                            //이미 a-b-c-d는 매칭되어있었음.todo: a-b가 이번에 처음 매칭됐기 때문에 이 코드도 필요없는것 같다. 검토 필요
                            //if (matched_4.get(A_Id).contains(bcd)) continue;

                            //a-b-c-d 매칭 성공
                            Optional<WaitingMember> D = waitingMemberRepository.findById(D_Id);
                            while (memberList.size() > 3) memberList.remove(memberList.size() - 1);
                            memberList.add(D.get());
                            getDirection(makePayload(memberList));

                            //if (!matched_4.containsKey(A_Id)) matched_4.put(A_Id, new HashSet<>());
                            matched_4.get(A_Id).add(bcd);

                            List<Long> acd = new ArrayList<>(List.of(A_Id, C_Id, D_Id));
                            Collections.sort(acd);
                            if (!matched_4.containsKey(B_Id)) matched_4.put(B_Id, new HashSet<>());
                            matched_4.get(B_Id).add(acd);

                            List<Long> abd = new ArrayList<>(List.of(A_Id, B_Id, D_Id));
                            Collections.sort(abd);
                            if (!matched_4.containsKey(C_Id)) matched_4.put(C_Id, new HashSet<>());
                            matched_4.get(C_Id).add(abd);

                            List<Long> abc = new ArrayList<>(List.of(A_Id, B_Id, C_Id));
                            Collections.sort(abc);
                            if (!matched_4.containsKey(D_Id)) matched_4.put(D_Id, new HashSet<>());
                            matched_4.get(D_Id).add(abc);
                        }
                    }
                }
            }
//        System.out.println("[2]: sz : " + matched_2.size() + " / " + matched_2);
//        System.out.println("[3]: sz : " + matched_3.size() + " / " + matched_3);
//        System.out.println("[4]: sz : " + matched_4.size() + " / " + matched_4);
    }

    private void del(List<Long> go) {

    }
}