package com.CapStone.inu.taxi.domain.waitingmember;

import com.CapStone.inu.taxi.domain.driver.Driver;
import com.CapStone.inu.taxi.domain.driver.DriverRepository;
import com.CapStone.inu.taxi.domain.room.RoomService;
import com.CapStone.inu.taxi.domain.waitingmember.dto.WaitingMemberReqDto;
import com.CapStone.inu.taxi.global.common.StatusCode;
import com.CapStone.inu.taxi.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WaitingMemberService {
    private final WaitingMemberRepository waitingMemberRepository;
    private final DriverRepository driverRepository;
    private final RoomService roomService;

    @Transactional
    public void createWaitingMember(Long memberId, WaitingMemberReqDto waitingMemberReqDto) {
        WaitingMember member = waitingMemberReqDto.toEntity(memberId);
        if (!waitingMemberRepository.existsById(memberId))
            waitingMemberRepository.save(member);
    }

    //유저들이 모두 레디를 마쳐 택시를 타고 떠났다.
    @Transactional
    public void depart(List<Long> go, Long driverId) {

        //대기 목록에서 지우기
        for (Long go_userId : go) {
            WaitingMember deletedUser = waitingMemberRepository.findById(go_userId).orElseThrow(()-> new CustomException(StatusCode.MEMBER_NOT_EXIST));
            waitingMemberRepository.delete(deletedUser);
        }
        Driver driver = driverRepository.findById(driverId).orElseThrow(()-> new CustomException(StatusCode.DRIVER_NOT_EXIST));
        driverRepository.delete(driver);

        //결제 기록 남겨두기

        roomService.depart(go, driverId);
    }

    //유저가 매칭을 취소했다.
    @Transactional
    public void cancelMatching(Long userId) {
        waitingMemberRepository.deleteById(userId);
        roomService.cancelMatching(userId);
    }
}