package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // [12주차] 단계 1 : WebSocket 위에서 STOMP(구독 · 발행 약속)를 쓴다
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws"); // ① 연결 주소 — 기본값 = 같은 사이트(Origin)만 허용 (setAllowedOrigins("*") 금지)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");   // ② 서버 → 브라우저 : /topic 여러 명, /queue 한 사람
        registry.setApplicationDestinationPrefixes("/app"); // ③ 브라우저 → 서버 (오늘은 안 씀, 채팅용)
        registry.setUserDestinationPrefix("/user");         // ④ 특정 사용자에게 : /user/queue/notifications
    }
}
