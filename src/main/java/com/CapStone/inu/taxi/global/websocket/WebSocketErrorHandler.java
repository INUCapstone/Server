package com.CapStone.inu.taxi.global.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

import static com.CapStone.inu.taxi.global.common.StatusCode.ACCESS_TOKEN_INVALID;

@Slf4j
@Component
public class WebSocketErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        log.info(ex.getMessage());
        if (ex.getMessage().equals("UNAUTHORIZED")) {
            // StompHeaderAccessor로 stomp 프레임을 생성한다.
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
            //stomp의 기본 헤더를 수정가능하게 바꾼다. nativeheader같은 추가 헤더는 상관 없다.
            headerAccessor.setLeaveMutable(true);
            //http와 다르게 웹소켓은 stomp 프레임의 message 헤더에 상태코드를 표시한다.
            headerAccessor.setMessage(String.valueOf(ACCESS_TOKEN_INVALID.getStatus()));
            // StompHeaderAccessor로 생성한 stomp 프레임을 토대로 메시지를 생성해 프론트에게 보낸다.
            // SimplemessageTemplate은 구독자에게 보내는 메시지를 생성한다. 다른 개념이다.
            return MessageBuilder.createMessage(ACCESS_TOKEN_INVALID.getMessage().getBytes(StandardCharsets.UTF_8), headerAccessor.getMessageHeaders());

        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }
}
