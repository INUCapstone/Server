package com.CapStone.inu.taxi.domain.waitingmember;

import com.CapStone.inu.taxi.domain.room.RoomService;
import com.CapStone.inu.taxi.domain.waitingmember.dto.WaitingMemberReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class WaitingMemberController {
    private final WaitingMemberService waitingMemberService;
    private final RoomService roomService;

    // 경로는 pub/match/{memberId}로 메세지를 보내야한다.
    @MessageMapping("match/{memberId}")
    public void startMatching(@DestinationVariable Long memberId, @Payload WaitingMemberReqDto waitingMemberReqDto){
        log.info("웹소켓 연결 성공");
        waitingMemberService.createWaitingMember(memberId,waitingMemberReqDto);
        log.info("Waiting Member 생성 성공");
        roomService.matchUser(memberId);

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("웹소켓 연결 끊음");
        // StompHeaderAccessor를 통해 메시지에서 세션 정보를 가져옵니다.
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // 세션 속성에서 memberId를 가져옵니다.
        Long memberId = Long.parseLong((String) headerAccessor.getSessionAttributes().get("memberId"));
        waitingMemberService.cancelMatching(memberId);
        log.info("Waiting Member 삭제 성공");

    }
}