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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class WaitingMemberRoomService {

    private final WaitingMemberRoomRepository waitingMemberRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void makeWaitingMemberRoom(WaitingMember waitingMember, Room room) {
        WaitingMemberRoom waitingMemberRoom = new WaitingMemberRoom(waitingMember, room);
        waitingMemberRoomRepository.save(waitingMemberRoom);
    }

    public List<RoomRes> makeRoomResList(Long userId){

        List<RoomRes> roomResList = new ArrayList<>();

        //ID가 userId인 모든 WaitingMemberRoom 조회.
        List<WaitingMemberRoom> waitingMemberRoomList = waitingMemberRoomRepository.findByWaitingMember_Id(userId);

        for(WaitingMemberRoom waitingMemberRoom : waitingMemberRoomList){
            //userId가 속한 모든 room 에 대해,
            Room room = waitingMemberRoom.getRoom();
            List<WaitingMember> memberList = new ArrayList<>();
            //그 room 에 있는 waitingMember 조회.
            for(WaitingMemberRoom _waitingMemberRoom : room.getWaitingMemberRoomList()){
                memberList.add(_waitingMemberRoom.getWaitingMember());
            }
            //member 중복제거.
            memberList = memberList.stream().distinct().collect(Collectors.toList());

            List<memberInfo> memberInfoList = new ArrayList<>();
            //조회한 waitingMember 들로부터 memberInfo 추출.
            for (WaitingMember waitingMember : memberList) {
                Member member = memberRepository.findById(waitingMember.getId()).orElseThrow(()-> new CustomException(StatusCode.MEMBER_NOT_EXIST));
                memberInfo memberInfo = new memberInfo();
                memberInfo.setNickname(member.getNickname());
                memberInfo.setMemberId(member.getId());
                memberInfo.setIsReady(false);
                memberInfoList.add(memberInfo);
                //log.info("member name logging : " + member.getNickname());
            }

            //역직렬화.
            Gson gson = new Gson();
            Type listType = new TypeToken<List<pathInfo>>(){}.getType();
            List<pathInfo> pathInfoList = gson.fromJson(room.getTaxiPath(), listType);

            RoomRes roomRes = RoomRes.builder()
                    .roomId(room.getRoomId())
                    .currentMemberCnt(memberList.size())
                    .pathInfoList(pathInfoList)
                    .time(room.getTaxiDuration())
                    .charge(room.getTaxiFare())
                    .memberList(memberInfoList)
                    .isStart(room.getIsStart())
                    .build();

                    roomResList.add(roomRes);
        }
        return roomResList;
    }
}
