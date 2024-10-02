package com.CapStone.inu.taxi.domain.room.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class RoomResList {
    List<RoomRes> roomResList;

    @Builder
    public RoomResList(List<RoomRes> roomResList) {
        this.roomResList = roomResList;
    }
}