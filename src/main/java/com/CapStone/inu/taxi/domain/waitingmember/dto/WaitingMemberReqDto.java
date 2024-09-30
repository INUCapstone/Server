package com.CapStone.inu.taxi.domain.waitingmember.dto;

import com.CapStone.inu.taxi.domain.waitingmember.WaitingMember;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WaitingMemberReqDto {

    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;

    @Builder
    public WaitingMemberReqDto(Double startX, Double startY, Double endX, Double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public WaitingMember toEntity(Long memberId){
        return WaitingMember.builder()
                .id(memberId)
                .startX(startX)
                .startY(startY)
                .endX(endX)
                .endY(endY)
                .build();
    }
}
