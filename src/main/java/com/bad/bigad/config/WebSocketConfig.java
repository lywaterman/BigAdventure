package com.bad.bigad.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer
{
    @Autowired
    HeaderParamInterceptor headerParamInterceptor;

    @Autowired
    TokenCheckInterceptor tokenCheckInterceptor;

    @Autowired
    MyHandshakeHandler myHandshakeHandler;

    @Autowired
    WebSocketHandler webSocketHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //chat相关
        registry.addEndpoint("/chat")
                .addInterceptors(tokenCheckInterceptor)
                .setHandshakeHandler(myHandshakeHandler);

        registry.addEndpoint("/chat")
                .setAllowedOrigins("*")
                .withSockJS()
                .setHeartbeatTime(60_000);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/game")
                .addInterceptors(tokenCheckInterceptor)
                .setHandshakeHandler(myHandshakeHandler);

        registry.addHandler(webSocketHandler, "/game")
                .setAllowedOrigins("*")
                .withSockJS()
                .setHeartbeatTime(60_000);
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");

        //使用rabbitmq做代理，所有消息发送给rabbitmq
        registry.enableStompBrokerRelay("/exchange","/topic","/queue","/amq/queue")
                //.setVirtualHost("bad")
                .setRelayHost("localhost")
                .setClientLogin("guest")
                .setClientPasscode("guest")
                .setSystemLogin("guest")
                .setSystemPasscode("guest")
                .setSystemHeartbeatSendInterval(5000)
                .setSystemHeartbeatReceiveInterval(4000);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(headerParamInterceptor);
//    }
}