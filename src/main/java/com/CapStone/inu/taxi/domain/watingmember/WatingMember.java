package com.CapStone.inu.taxi.domain.watingmember;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WatingMember {
    private Long id;
    private String departure;
    private String arrival;

    @Builder
    public WatingMember(Long id, String departure, String arrival) {
        this.id = id;
        this.departure = departure;
        this.arrival = arrival;
    }
}
