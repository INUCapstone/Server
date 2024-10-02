package com.CapStone.inu.taxi.global.websocket;

import com.CapStone.inu.taxi.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketPreHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // 이미 존재하는 메시지의 헤더를 읽고 수정할 때 사용한다.
        StompHeaderAccessor headerAccessor= MessageHeaderAccessor.getAccessor(message,StompHeaderAccessor.class);
        if(StompCommand.CONNECT==headerAccessor.getCommand()){
            // Authorization을 가지는 첫번째 사용자 정의 헤더를 꺼낸다.
            String accessToken= headerAccessor.getFirstNativeHeader("Authorization");
            if(accessToken==null || !accessToken.startsWith("Bearer ")){
                throw new MessageDeliveryException("UNAUTHORIZED");
            }

            accessToken = accessToken.substring(7);
            if(jwtTokenProvider.validateToken(accessToken)){
                Authentication authentication=jwtTokenProvider.getAuthentication(accessToken);
                headerAccessor.setUser(authentication);
                headerAccessor.getSessionAttributes().put("nickname",jwtTokenProvider.getNickname(accessToken));
                headerAccessor.getSessionAttributes().put("memberId",jwtTokenProvider.getMemberId(accessToken));

                return message;
            }
            else
                throw new MessageDeliveryException("UNAUTHORIZED");
        }
        return message;


    }
}

