package com.CapStone.inu.taxi.domain.waitingmemberRoom;

import com.CapStone.inu.taxi.domain.room.Room;
import com.CapStone.inu.taxi.domain.waitingmember.WaitingMember;
import com.CapStone.inu.taxi.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaitingMemberRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isReady;

    @ManyToOne
    @JoinColumn(name = "waitingmember_id", nullable = false)
    private WaitingMember waitingMember;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public void setReady(Boolean isReady) {
        this.isReady = isReady;
    }

    @Builder
    public WaitingMemberRoom(WaitingMember waitingMember, Room room) {
        this.isReady = false;
        this.waitingMember = waitingMember;
        this.room = room;
    }
}
