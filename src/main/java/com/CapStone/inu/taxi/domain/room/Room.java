package com.CapStone.inu.taxi.domain.room;

import com.CapStone.inu.taxi.domain.waitingmemberRoom.WaitingMemberRoom;
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

    /*generated value가 아닌, 탑승한 유저들의 id정보를 모두 담고있는 해시값 느낌으로 id생성.(방 중복 생성을 피하기 위함.)
    waitingMember의 id는 member를 따르고, member의 id 생성 전략인 identity는 mariadb에선 auto increment인데,
    mariadb에선 중간에 데이터 삭제가 일어나도 자동으로 부여되는 값의 중복이 일어나지 않는다고하니, 괜찮을듯하다.
    만약 윗 문장이 거짓이라면, 데이터 삭제에 따라 roomId의 중복이 발생할 수 있으니 고쳐야한다.*/
    @Id
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

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaitingMemberRoom> waitingMemberRoomList = new ArrayList<>();

    @Builder
    private Room(Long roomId, Integer taxiFare, Integer taxiDuration, String taxiPath, Integer taxiHeadcount,
                 Long driverId) {
        this.roomId = roomId;
        this.taxiFare = taxiFare;
        this.taxiDuration = taxiDuration;
        this.taxiPath = taxiPath;
        this.taxiHeadcount = taxiHeadcount;
        this.driverId = driverId;
        isStart = false;
    }
}
