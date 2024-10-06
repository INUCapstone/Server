package com.CapStone.inu.taxi.domain.waitingmember;

import com.CapStone.inu.taxi.domain.waitingmember.dto.WaitingMemberReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RestController
@RequiredArgsConstructor
public class WaitingMemberController {
    private final WaitingMemberService waitingMemberService;

    // 경로는 pub/match/{memberId}로 메세지를 보내야한다.
    @MessageMapping("match/{memberId}")
    public void startMatching(@DestinationVariable Long memberId, @Payload WaitingMemberReqDto waitingMemberReqDto){
        waitingMemberService.createWaitingMember(memberId,waitingMemberReqDto);
        waitingMemberService.Test();

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        // StompHeaderAccessor를 통해 메시지에서 세션 정보를 가져옵니다.
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // 세션 속성에서 memberId를 가져옵니다.
        Long memberId = Long.parseLong((String) headerAccessor.getSessionAttributes().get("memberId"));
        waitingMemberService.cancelMatching(memberId);

    }

    @GetMapping(value = "/test")
    public void test() {
        waitingMemberService.Test();
    }
}