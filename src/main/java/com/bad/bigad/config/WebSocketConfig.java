package com.bad.bigad.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS()
                .setHeartbeatTime(60_000);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app/");
        registry.setUserDestinationPrefix("/user/");
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")
                .setRelayPort(5672)
                .setClientLogin("guest")
                .setClientPasscode("guest");

    }
}