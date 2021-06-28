package com.bad.bigad.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    HeaderParamInterceptor headerParamInterceptor;

    @Autowired
    TokenCheckInterceptor tokenCheckInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").addInterceptors(tokenCheckInterceptor)
                .setHandshakeHandler(
                        new DefaultHandshakeHandler() {
                            @Override
                            protected Principal determineUser(ServerHttpRequest request,
                                                              WebSocketHandler wsHandler,
                                                              Map<String, Object> attributes) {
                                //设置认证用户
                                return (Principal)attributes.get("user");
                            }
                        });

        registry.addEndpoint("/chat")
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
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(headerParamInterceptor);
//    }
}