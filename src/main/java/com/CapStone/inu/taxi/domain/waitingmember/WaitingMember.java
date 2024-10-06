package com.CapStone.inu.taxi.domain.waitingmember;

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
public class WaitingMember extends BaseEntity {

    @Id
    private Long id;

    @Column(name = "start_x", nullable = false)
    private Double startX;

    @Column(name = "start_y", nullable = false)
    private Double startY;

    @Column(name = "end_x", nullable = false)
    private Double endX;

    @Column(name = "end_y", nullable = false)
    private Double endY;

    @Builder
    private WaitingMember(Long id, Double startX, Double startY, Double endX, Double endY) {
        this.id=id;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @OneToMany(mappedBy = "waitingMember", fetch = FetchType.LAZY)
    private List<WaitingMemberRoom> waitingMemberRoomList=new ArrayList<>();
}
