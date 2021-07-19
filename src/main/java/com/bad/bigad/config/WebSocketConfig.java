package com.bad.bigad.config;

import com.bad.bigad.component.GameSocketHandler;
import com.bad.bigad.manager.ScriptManager;
import com.bad.bigad.manager.WsSessionManager;
import com.bad.bigad.model.ChatMessage;
import com.bad.bigad.service.ChatService;
import com.bad.bigad.util.BridgeForJs;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

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
    WsSessionManager wsSessionManager;

    @Autowired
    ChatService chatService;

    private final MessageChannel outChannel;

    @Autowired
    public WebSocketConfig(MessageChannel clientOutboundChannel) {
        this.outChannel = clientOutboundChannel;
    }


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

    //EndPoint有效
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(final WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {

                    @Override
                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                        super.afterConnectionEstablished(session);
                        // We will store current user's session into WebsocketSessionHolder after connection is established
                        String username = session.getPrincipal().getName();
                        Long id = Long.parseLong(username);

                        WebSocketSession curSession = wsSessionManager.getChatSession(id);

                        if (curSession != null) {
//                            ChatMessage msg = new ChatMessage();
//                            msg.setSender("sys");
//                            msg.setTo(username);
//                            msg.setToUser(true);
//                            msg.setType(ChatMessage.MessageType.ANOTHER);
//                            msg.setContent("您在其他地方登陆了");
//                            chatService.sendMsg(msg);
                            wsSessionManager.removeAddCloseChatSession(id);
                        }

                        wsSessionManager.addChatSession(id, session);

                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        super.afterConnectionClosed(session, closeStatus);
                        String username = session.getPrincipal().getName();
                        Long id = Long.parseLong(username);

                        wsSessionManager.removeChatSession(id, session);
                    }
                };
            }
        });
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(headerParamInterceptor);
//    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> inMessage, MessageChannel channel) {
                StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(inMessage);
                String des = inAccessor.getDestination();
                if (StompCommand.SUBSCRIBE.equals(inAccessor.getCommand())) {
                    //可以限定只能订阅的频道
                }

                if (StompCommand.SEND.equals(inAccessor.getCommand())) {
                    if (des == null || !des.equals("/app/chat.sendMsg")) {
                        throw new IllegalArgumentException("can only pushlish message to /app/chat/sendMsg");
                    }
                }

                return inMessage;
            }

            @Override
            public void postSend(Message<?> inMessage, MessageChannel channel, boolean sent) {

                StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(inMessage);
                String des = inAccessor.getDestination();
                if (des == null || !des.equals("/app/chat.sendMsg")) {
                    return;
                }
                String receipt = inAccessor.getReceipt();
                if (StringUtils.isEmpty(receipt)) {
                    return;
                }

                StompHeaderAccessor outAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
                outAccessor.setSessionId(inAccessor.getSessionId());
                outAccessor.setReceiptId(receipt);
                outAccessor.setLeaveMutable(true);

                Message<byte[]> outMessage =
                        MessageBuilder.createMessage(new byte[0], outAccessor.getMessageHeaders());

                outChannel.send(outMessage);
            }
        });
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//
//        registration.interceptors(new ExecutorChannelInterceptor() {
//
//            @Override
//            public void afterMessageHandled(Message<?> inMessage,
//                                            MessageChannel inChannel, MessageHandler handler, Exception ex) {
//
//                StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(inMessage);
//                String receipt = inAccessor.getReceipt();
//                if (StringUtils.isEmpty(receipt)) {
//                    return;
//                }
//
//                StompHeaderAccessor outAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT);
//                outAccessor.setSessionId(inAccessor.getSessionId());
//                outAccessor.setReceiptId(receipt);
//                outAccessor.setLeaveMutable(true);
//
//                Message<byte[]> outMessage =
//                        MessageBuilder.createMessage(new byte[0], outAccessor.getMessageHeaders());
//
//                outChannel.send(outMessage);
//            }
//        });
//    }

    //SimpleBrokerMessageHandler doesn't support RECEIPT frame, hence we emulate it this way
//    @Bean
//    public ApplicationListener<SessionSubscribeEvent> webSocketEventListener(
//            final AbstractSubscribableChannel clientOutboundChannel) {
//        return event -> {
//            Message<byte[]> message = event.getMessage();
//            StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
//            if (stompHeaderAccessor.getReceipt() != null) {
//                stompHeaderAccessor.setHeader("stompCommand", StompCommand.RECEIPT);
//                stompHeaderAccessor.setReceiptId(stompHeaderAccessor.getReceipt());
//                clientOutboundChannel.send(
//                        MessageBuilder.createMessage(new byte[0], stompHeaderAccessor.getMessageHeaders()));
//            }
//        };
//    }
}