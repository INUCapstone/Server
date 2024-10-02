package com.CapStone.inu.taxi.domain.room;

import com.CapStone.inu.taxi.domain.waitingmember.WaitingMember;
import com.CapStone.inu.taxi.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(name = "taxi_fare", nullable = false)
    private Integer taxiFare;

    @Column(name = "taxi_duration", nullable = false)
    private Integer taxiDuration;

    @Column(name = "taxi_path", nullable = false, columnDefinition = "TEXT")
    private String taxiPath;

    @Column(name = "taxi_headcount", nullable = false)
    private Integer taxiHeadcount;

    @Column(name = "driver_id")//방 생성될 시점에는 null 가능.
    private Long driverId;

    @Column(name = "is_start", nullable = false)
    private Boolean isStart;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete;

    @OneToMany(mappedBy = "room", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<WaitingMember> waitingMemberList;

    @Builder
    private Room(Integer taxiFare, Integer taxiDuration, String taxiPath, Integer taxiHeadcount,
                 Long driverId, List<WaitingMember> waitingMemberList) {
        this.taxiFare = taxiFare;
        this.taxiDuration = taxiDuration;
        this.taxiPath = taxiPath;
        this.taxiHeadcount = taxiHeadcount;
        this.driverId = driverId;
        isStart = false;
        isDelete = false;
        this.waitingMemberList = waitingMemberList;
    }
}
