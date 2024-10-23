package com.CapStone.inu.taxi.domain.waitingmember;

import com.CapStone.inu.taxi.domain.room.RoomService;
import com.CapStone.inu.taxi.domain.waitingmember.dto.WaitingMemberReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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