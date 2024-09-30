package com.CapStone.inu.taxi.domain.waitingmember;

import com.CapStone.inu.taxi.domain.waitingmember.dto.WaitingMemberReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WaitingMemberController {
    private final WaitingMemberService waitingMemberService;

    @MessageMapping("match/{memberId}")
    public void startMatching(@DestinationVariable Long memberId, @Payload WaitingMemberReqDto waitingMemberReqDto){
        waitingMemberService.startMatching(memberId,waitingMemberReqDto);
    }

    @GetMapping(value = "/test")
    public void test() {
        waitingMemberService.matchUser();
    }
}