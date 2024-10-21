package com.CapStone.inu.taxi.domain.waitingmemberRoom;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class WaitingMemberRoomController {
    private final WaitingMemberRoomService waitingMemberRoomService;


    // 경로는 pub/match/{memberId}로 메세지를 보내야한다.
    @MessageMapping("ready/{roomId}/{memberId}")
    public void startMatching(@DestinationVariable Long roomId, @DestinationVariable Long userId){


    }
}
