package com.CapStone.inu.taxi.domain.waitingroom.dto.response;

import com.CapStone.inu.taxi.domain.waitingroom.WaitingRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WaitingRoomRes {

    private final String title;
    private final Integer memberCount;
    private final String departure;
    private final String arrival;
    private final String layover;
    private final LocalDateTime expectedTime;
    private final Integer expectedPrice;

    @Builder
    private WaitingRoomRes(String title, Integer memberCount, String departure, String arrival, String layover, LocalDateTime expectedTime, Integer expectedPrice) {
        this.title = title;
        this.memberCount = memberCount;
        this.departure = departure;
        this.arrival = arrival;
        this.layover = layover;
        this.expectedTime = expectedTime;
        this.expectedPrice = expectedPrice;
    }

    public static WaitingRoomRes from(WaitingRoom waitingRoom){
        return WaitingRoomRes.builder()
                .title(waitingRoom.getTitle())
                .memberCount(waitingRoom.getMemberCount())
                .departure(waitingRoom.getDeparture())
                .arrival(waitingRoom.getArrival())
                .layover(waitingRoom.getLayover())
                .expectedTime(waitingRoom.getExpectedTime())
                .expectedPrice(waitingRoom.getExpectedPrice())
                .build();
    }
}
