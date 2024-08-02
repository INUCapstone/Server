package com.CapStone.inu.taxi.domain.requirement.dto.response;

import com.CapStone.inu.taxi.domain.requirement.Requirement;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RequirementRes {

    private final Long requirementId;
    private final LocalDateTime limitTime;
    private final Long limitPrice;
    private final String departure;
    private final String arrival;

    @Builder
    private RequirementRes(Long requirementId, LocalDateTime limitTime, Long limitPrice, String departure, String arrival) {
        this.requirementId = requirementId;
        this.limitTime = limitTime;
        this.limitPrice = limitPrice;
        this.departure = departure;
        this.arrival = arrival;
    }

    public static RequirementRes from(Requirement requirement){
        return RequirementRes.builder()
                .requirementId(requirement.getId())
                .limitTime(requirement.getLimitTime())
                .limitPrice(requirement.getLimitPrice())
                .departure(requirement.getDeparture())
                .arrival(requirement.getArrival())
                .build();


    }
}
