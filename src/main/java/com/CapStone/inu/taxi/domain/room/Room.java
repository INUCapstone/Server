package com.CapStone.inu.taxi.domain.room;

import com.CapStone.inu.taxi.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(name = "matched_user_list", nullable = false)
    private String matchedUserList;

    //유저들의 레디 여부도 저장해야겠다.

    @Column(name = "taxi_fare", nullable = false)
    private Integer taxiFare;

    @Column(name = "taxi_duration", nullable = false)
    private Integer taxiDuration;

    @Column(name = "taxi_path", nullable = false)
    private String taxiPath;

    @Column(name = "taxi_headcount", nullable = false)
    private Integer taxiHeadcount;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

}
