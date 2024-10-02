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
    private final Boolean isDelete;
    private final Boolean isStart;

    @Builder
    public RoomRes(Long roomId, Integer currentMemberCnt, List<pathInfo> pathInfoList, Integer time,
                   Integer charge, List<memberInfo> memberList, Boolean isDelete, Boolean isStart) {
        this.roomId = roomId;
        this.currentMemberCnt = currentMemberCnt;
        this.pathInfoList = pathInfoList;
        this.time = time;
        this.charge = charge;
        this.memberList = memberList;
        this.isDelete = isDelete;
        this.isStart = isStart;
    }
}