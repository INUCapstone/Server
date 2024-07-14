package com.CapStone.inu.taxi.domain.room;


import com.CapStone.inu.taxi.domain.blacklist.BlackList;
import com.CapStone.inu.taxi.domain.member.Member;
import com.CapStone.inu.taxi.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false,name = "member_count")
    private Integer memberCount;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String arrival;

    private String layover;

    @Column(nullable = false, name = "expected_time")
    private String expectedTime;

    @Column(nullable = false, name = "expected_price")
    private String expectedPrice;

    @OneToMany(mappedBy = "room",fetch = FetchType.LAZY)
    private List<Member> memberList=new ArrayList<>();

    @OneToMany(mappedBy = "room",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<BlackList> blackLists=new ArrayList<>();

    //빌더로만 생성하게끔 제한하고싶어서 생성자의 접근자를 private으로 설정
    @Builder
    private Room(String title, Integer memberCount, String departure, String arrival, String layover, String expectedTime, String expectedPrice) {
        this.title = title;
        this.memberCount = memberCount;
        this.departure = departure;
        this.arrival = arrival;
        this.layover = layover;
        this.expectedTime = expectedTime;
        this.expectedPrice = expectedPrice;
    }
}
