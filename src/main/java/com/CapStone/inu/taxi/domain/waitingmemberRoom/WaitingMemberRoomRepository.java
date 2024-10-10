package com.CapStone.inu.taxi.domain.waitingmemberRoom;

import com.CapStone.inu.taxi.domain.waitingmember.WaitingMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaitingMemberRoomRepository extends JpaRepository<WaitingMemberRoom, Long> {

    List<WaitingMemberRoom> findByWaitingMember(Long userId);
}
