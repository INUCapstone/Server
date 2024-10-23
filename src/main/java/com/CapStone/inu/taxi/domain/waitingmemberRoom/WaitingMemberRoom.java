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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"waitingmember_id", "room_id"})
        }
)
public class WaitingMemberRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer charge;

    @Column(nullable = false)
    private Integer time;

    @Column(nullable = false)
    private Boolean isReady;

    @ManyToOne
    @JoinColumn(name = "waitingmember_id", nullable = false)
    private WaitingMember waitingMember;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Builder
    public WaitingMemberRoom(WaitingMember waitingMember, Room room, Integer time, Integer charge) {
        this.isReady = false;
        this.waitingMember = waitingMember;
        this.room = room;
        this.time = time;
        this.charge = charge;
    }

    public void updateReady() {
        this.isReady = !isReady;
    }
}