package com.CapStone.inu.taxi.domain.waitingmemberRoom;

import com.CapStone.inu.taxi.domain.member.Member;
import com.CapStone.inu.taxi.domain.member.MemberRepository;
import com.CapStone.inu.taxi.domain.room.Room;
import com.CapStone.inu.taxi.domain.room.dto.kakao.memberInfo;
import com.CapStone.inu.taxi.domain.room.dto.kakao.pathInfo;
import com.CapStone.inu.taxi.domain.room.dto.response.RoomRes;
import com.CapStone.inu.taxi.domain.waitingmember.WaitingMember;
import com.CapStone.inu.taxi.global.common.StatusCode;
import com.CapStone.inu.taxi.global.exception.CustomException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.CapStone.inu.taxi.global.common.StatusCode.ROOM_MEMBER_NOT_EXIST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class WaitingMemberRoomService {

    private final WaitingMemberRoomRepository waitingMemberRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void makeWaitingMemberRoom(WaitingMember waitingMember, Room room, Integer time, Integer charge) {
        WaitingMemberRoom waitingMemberRoom = new WaitingMemberRoom(waitingMember, room, time, charge);

        //기존에 있던 waitingMemberRoom이면, 삭제하고 다시 넣는다.
        //(room은 Id로 체크하기때문에 자동으로 update가 됐지만, 이 경우는 중복된 값이 추가로 들어가게 되는 문제가 있었다.)
        if (waitingMemberRoomRepository.findByRoom_RoomIdAndWaitingMember_Id(room.getRoomId(), waitingMemberRoom.getId()).isPresent())
            waitingMemberRoomRepository.deleteByRoom_RoomIdAndWaitingMember_Id(room.getRoomId(), waitingMemberRoom.getId());
        waitingMemberRoomRepository.save(waitingMemberRoom);
    }

    public List<RoomRes> makeAllRoomResList(Long userId) {

        List<RoomRes> roomResList = new ArrayList<>();

        //ID가 userId인 모든 WaitingMemberRoom 조회.
        List<WaitingMemberRoom> waitingMemberRoomList = waitingMemberRoomRepository.findByWaitingMember_Id(userId);

        for (WaitingMemberRoom waitingMemberRoom : waitingMemberRoomList) {
            Room room = waitingMemberRoom.getRoom();
            roomResList.add(makeRoomRes(room, userId));
        }
        return roomResList;
    }

    public RoomRes makeRoomRes(Room room, Long userId) {
        List<WaitingMember> memberList = new ArrayList<>();
        //userId가 속한 room 에 대해, 그 room 에 있는 waitingMember 조회.
        for (WaitingMemberRoom _waitingMemberRoom : room.getWaitingMemberRoomList()) {
            memberList.add(_waitingMemberRoom.getWaitingMember());
        }
        //member 중복제거.
        memberList = memberList.stream().distinct().collect(Collectors.toList());

        List<memberInfo> memberInfoList = new ArrayList<>();
        //조회한 waitingMember 들로부터 memberInfo 추출.
        for (WaitingMember waitingMember : memberList) {
            Member member = memberRepository.findById(waitingMember.getId()).orElseThrow(() -> new CustomException(StatusCode.MEMBER_NOT_EXIST));

            WaitingMemberRoom waitingMemberRoom = waitingMemberRoomRepository.
                    findByRoom_RoomIdAndWaitingMember_Id(room.getRoomId(), waitingMember.getId()).orElseThrow(() -> new CustomException(ROOM_MEMBER_NOT_EXIST));

            memberInfo memberInfo = new memberInfo();
            memberInfo.setNickname(member.getNickname());
            memberInfo.setMemberId(member.getId());
            memberInfo.setIsReady(waitingMemberRoom.getIsReady());
            memberInfoList.add(memberInfo);
            //log.info("member name logging : " + member.getNickname());
        }

        //택시 경로 -> 역직렬화.
        Gson gson = new Gson();
        Type listType = new TypeToken<List<pathInfo>>() {
        }.getType();
        List<pathInfo> pathInfoList = gson.fromJson(room.getTaxiPath(), listType);

        log.info(String.valueOf(room.getRoomId()));
        log.info(String.valueOf(userId));
        WaitingMemberRoom waitingMemberRoom = waitingMemberRoomRepository.findByRoom_RoomIdAndWaitingMember_Id(room.getRoomId(), userId)
                .orElseThrow(() -> new CustomException(StatusCode.MEMBER_NOT_EXIST));

        return RoomRes.builder()
                .roomId(room.getRoomId())
                .currentMemberCnt(memberList.size())
                .pathInfoList(pathInfoList)
                .time(waitingMemberRoom.getTime())
                .charge(waitingMemberRoom.getCharge())
                .memberList(memberInfoList)
                .isStart(room.getIsStart())
                .build();
    }
}
