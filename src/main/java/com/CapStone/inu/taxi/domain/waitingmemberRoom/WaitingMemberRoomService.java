package com.CapStone.inu.taxi.domain.waitingmemberRoom;

import com.CapStone.inu.taxi.domain.member.Member;
import com.CapStone.inu.taxi.domain.member.MemberRepository;
import com.CapStone.inu.taxi.domain.room.Room;
import com.CapStone.inu.taxi.domain.room.dto.kakao.memberInfo;
import com.CapStone.inu.taxi.domain.room.dto.kakao.pathInfo;
import com.CapStone.inu.taxi.domain.room.dto.response.RoomRes;
import com.CapStone.inu.taxi.domain.waitingmember.WaitingMember;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WaitingMemberRoomService {

    private final WaitingMemberRoomRepository waitingMemberRoomRepository;
    private final MemberRepository memberRepository;

    public void makeWaitingMemberRoom(WaitingMember waitingMember, Room room) {
        WaitingMemberRoom waitingMemberRoom = new WaitingMemberRoom(waitingMember, room);
        waitingMemberRoomRepository.save(waitingMemberRoom);
    }

    public List<RoomRes> makeRoomResList(Long userId){

        List<RoomRes> roomResList = new ArrayList<>();

        //이거 고쳐야될지도.
        List<WaitingMemberRoom> waitingMemberRoomList = waitingMemberRoomRepository.findByWaitingMember(userId);

        for(WaitingMemberRoom waitingMemberRoom : waitingMemberRoomList){
            Room room = waitingMemberRoom.getRoom();
            List<WaitingMember> memberList = new ArrayList<>();
            //룸에 누가 들어있는지 찾기위해. 변수명 바꾸기.
            for(WaitingMemberRoom _asdasdawaitingMemberRoom : room.getWaitingMemberRoomList()){
                memberList.add(_asdasdawaitingMemberRoom.getWaitingMember());
            }

            List<memberInfo> memberInfoList = new ArrayList<>();
            for (WaitingMember waitingMember : memberList) {
                Member member = memberRepository.findById(waitingMember.getId()).orElseThrow(IllegalArgumentException::new);
                memberInfo memberInfo = new memberInfo();
                memberInfo.setNickname(member.getNickname());
                memberInfo.setMemberId(member.getId());
                memberInfo.setIsReady(false);
                memberInfoList.add(memberInfo);
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
                    .isDelete(room.getIsDelete())
                    .isStart(room.getIsStart())
                    .build();

            roomResList.add(roomRes);
        }

        return roomResList;
    }
}
