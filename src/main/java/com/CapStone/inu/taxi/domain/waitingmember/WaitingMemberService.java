package com.CapStone.inu.taxi.domain.waitingmember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WaitingMemberService {
    private final WaitingMemberRepository waitingMemberRepository;

    //유저들이 모두 레디를 마쳐 택시를 타고 떠났다.
    @Transactional
    public void depart(List<Long> go) {
        //대기 목록에서 지우기
        for (Long go_userId : go) {
            WaitingMember deletedUser = waitingMemberRepository.findById(go_userId).orElseThrow(IllegalArgumentException::new);
            waitingMemberRepository.delete(deletedUser);
        }
    }

    //유저가 매칭을 취소했다.
    @Transactional
    public void cancel(Long userId) {
        WaitingMember deletedUser = waitingMemberRepository.findById(userId).orElseThrow(IllegalArgumentException::new);
        waitingMemberRepository.delete(deletedUser);
    }
}