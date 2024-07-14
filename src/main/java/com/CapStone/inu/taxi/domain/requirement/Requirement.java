package com.CapStone.inu.taxi.domain.requirement;


import com.CapStone.inu.taxi.domain.member.Member;
import com.CapStone.inu.taxi.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Requirement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "limit_time")
    private LocalDateTime limitTime;

    @Column(nullable = false, name = "limit_price")
    private Long limitPrice;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String arrival;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @Builder
    private Requirement(LocalDateTime limitTime, Long limitPrice, String departure, String arrival, Member member) {
        this.limitTime = limitTime;
        this.limitPrice = limitPrice;
        this.departure = departure;
        this.arrival = arrival;
        this.member = member;
    }
}
