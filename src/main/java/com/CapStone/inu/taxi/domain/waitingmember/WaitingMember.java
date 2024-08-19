package com.CapStone.inu.taxi.domain.waitingmember;

import com.CapStone.inu.taxi.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaitingMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "start_x", nullable = false)
    private Double startX;

    @Column(name = "start_y", nullable = false)
    private Double startY;

    @Column(name = "end_x", nullable = false)
    private Double endX;

    @Column(name = "end_y", nullable = false)
    private Double endY;

    @Builder
    private WaitingMember(Double startX, Double startY, Double endX, Double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}
