package com.CapStone.inu.taxi.global.config;

import com.CapStone.inu.taxi.global.websocket.WebSocketErrorHandler;
import com.CapStone.inu.taxi.global.websocket.WebSocketPreHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// message-broker는 메시징 미들웨어로 어플리케이션 간에 통신을 수행을 함으로써, 서로 다른 언어나 플랫폼으로 개발되도 통신이 가능
// message-queue를 사용해 데이터 패킷을 순서대로 저장해 소비될 때까지 갖고 있어 손실을 방지하고, 장애가 발생해도 시스템 동작 가능
@Configuration
@EnableWebSocketMessageBroker // 웹소켓 메시지 핸들링 활성화
@RequiredArgsConstructor
public class WebSocket implements WebSocketMessageBrokerConfigurer {
    private final WebSocketPreHandler webSocketPreHandler;
    private final WebSocketErrorHandler webSocketErrorHandler;

    //stomp 사용(웹소켓 위에서 사용됨)- 클라이언트와 서버가 전송할 메세지의 유형, 형식, 내용들을 정의하는 매커니즘이다.
    //메세징 프로토콜과 메세징 형식을 개발할 필요가 없다.
    //중개 서버를 통한 클라이언트간에 비동기적 메시지 전송을 위한 프로토콜
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // sockJs는 기본적으로 웹소켓을 사용하지만 웹소켓을 지원하지 않는 브라우저는 대체 방법을 찾아주는 라이브러리이다.
        registry.addEndpoint("/ws") // handshake endpoint을 지정한다. 채팅 요청할 때 사용
                .setAllowedOriginPatterns("*"); //허용할 origin 패턴을 지정한다. (CORS 설정)
        //.withSockJS();
        registry.setErrorHandler(webSocketErrorHandler);
    }

    //메시지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/sub"으로 구독을 한다.
        registry.enableSimpleBroker("/sub");
        // 사용자가 채팅을 보내면 메시지 브로커가 "/pub"으로 날라온 메시지를 구독자들에게 보낸다.
        registry.setApplicationDestinationPrefixes("/pub");
    }

    //인터셉터 설정(메시지 보내기전 jwt 검사 및 유효성 검사)
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketPreHandler);
    }

}

