package com.CapStone.inu.taxi.domain.room.dto.response;

import com.CapStone.inu.taxi.domain.room.dto.kakao.*;
import lombok.Builder;
import lombok.Getter;

import java.util.*;

@Getter
public class RoomRes {
    private final Long roomId;
    private final Integer currentMemberCnt;
    private final List<pathInfo> pathInfoList;
    private final Integer time;
    private final Integer charge;
    private final List<memberInfo> memberList;
    private final Boolean isStart;

    //테스트용
    @Override
    public String toString() {
        String s = "[";
        for (memberInfo memberInfo: memberList)
            s=s.concat(memberInfo.getNickname() + ", ");
        s=s.concat("]");
        return s;
    }

    @Builder
    public RoomRes(Long roomId, Integer currentMemberCnt, List<pathInfo> pathInfoList, Integer time,
                   Integer charge, List<memberInfo> memberList, Boolean isStart) {
        this.roomId = roomId;
        this.currentMemberCnt = currentMemberCnt;
        this.pathInfoList = pathInfoList;
        this.time = time;
        this.charge = charge;
        this.memberList = memberList;
        this.isStart = isStart;
    }
}