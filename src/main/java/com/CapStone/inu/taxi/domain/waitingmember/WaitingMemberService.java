package com.CapStone.inu.taxi.domain.waitingmember;

import com.CapStone.inu.taxi.domain.driver.Driver;
import com.CapStone.inu.taxi.domain.driver.DriverRepository;
import com.CapStone.inu.taxi.domain.receipt.Receipt;
import com.CapStone.inu.taxi.domain.receipt.ReceiptRepository;
import com.CapStone.inu.taxi.domain.room.Room;
import com.CapStone.inu.taxi.domain.room.RoomService;
import com.CapStone.inu.taxi.domain.waitingmember.dto.WaitingMemberReqDto;
import com.CapStone.inu.taxi.domain.waitingmemberRoom.WaitingMemberRoom;
import com.CapStone.inu.taxi.global.common.State;
import com.CapStone.inu.taxi.global.common.StatusCode;
import com.CapStone.inu.taxi.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

//이거 모든 함수에 transaction이 달려있는데, 맨 위에만 달면 되는거아닌가?
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WaitingMemberService {
    private final WaitingMemberRepository waitingMemberRepository;
    private final RoomService roomService;

    @Transactional
    public void createWaitingMember(Long memberId, WaitingMemberReqDto waitingMemberReqDto) {
        WaitingMember member = waitingMemberReqDto.toEntity(memberId);
        if (!waitingMemberRepository.existsById(memberId))
            waitingMemberRepository.save(member);
    }

    //유저가 매칭을 취소했다.
    @Transactional
    public void cancelMatching(Long userId) {
        waitingMemberRepository.deleteById(userId);
        roomService.cancelMatching(userId);
    }
}