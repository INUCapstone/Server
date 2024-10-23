package com.CapStone.inu.taxi.domain.waitingmemberRoom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaitingMemberRoomRepository extends JpaRepository<WaitingMemberRoom, Long> {

    List<WaitingMemberRoom> findByWaitingMember_Id(Long waitingMemberId);

    List<WaitingMemberRoom> findByRoom_RoomId(Long roomId);

    Optional<WaitingMemberRoom> findByRoom_RoomIdAndWaitingMember_Id(Long roomId, Long memberId);

    void deleteByRoom_RoomIdAndWaitingMember_Id(Long roomId, Long memberId);
}
