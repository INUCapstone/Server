package com.CapStone.inu.taxi.domain.waitingroom;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class WaitingRoom{

    private String id;
    private String title;
    private Integer memberCount;
    private String departure;
    private String arrival;
    private String layover;
    private LocalDateTime expectedTime;
    private Integer expectedPrice;

    //빌더로만 생성하게끔 제한하고싶어서 생성자의 접근자를 private으로 설정
    @Builder
    private WaitingRoom(String id, String title, Integer memberCount, String departure, String arrival, String layover, LocalDateTime expectedTime, Integer expectedPrice) {
        this.id= id;
        this.title = title;
        this.memberCount = memberCount;
        this.departure = departure;
        this.arrival = arrival;
        this.layover = layover;
        this.expectedTime = expectedTime;
        this.expectedPrice = expectedPrice;
    }
}
