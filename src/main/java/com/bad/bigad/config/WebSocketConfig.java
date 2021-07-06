package com.bad.bigad.config;

import com.bad.bigad.component.ChatSocketHandler;
import com.bad.bigad.component.GameSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.*;

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
    GameSocketHandler gameSocketHandler;

    @Autowired
    ChatSocketHandler chatSocketHandler;

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
        registry.addHandler(gameSocketHandler, "/game")
                .addInterceptors(tokenCheckInterceptor)
                .setHandshakeHandler(myHandshakeHandler);

        registry.addHandler(gameSocketHandler, "/game")
                .setAllowedOrigins("*")
                .withSockJS()
                .setHeartbeatTime(60_000);

//        registry.addHandler(chatSocketHandler, "/chat");
//        registry.addHandler(chatSocketHandler, "/chat")
//                .setAllowedOrigins("*")
//                .withSockJS()
//                .setHeartbeatTime(60_000);
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