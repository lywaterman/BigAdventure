package com.bad.bigad.config;

import com.bad.bigad.util.Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;

@Component
public class HeaderParamInterceptor implements ExecutorChannelInterceptor {
//    @Value("${jwt_key}")
//    String jwtKey;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
//                message, StompHeaderAccessor.class);
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
//            if (raw instanceof Map) {
//                Object token = ((Map) raw).get("token");
//
//                if (token instanceof LinkedList) {
//                    String jwtToken = ((LinkedList) token).get(0).toString();
//                    Map<String, Object> claims = Util.instance.parseToken(
//                            ((LinkedList) token).get(0).toString(),
//                            jwtKey);
//                    if (claims != null) {
//                        accessor.setUser(new UserPrincipal((String) claims.get("wx_name")));
//                    } else {
//                        return null;
//                    }
//
//                }
//            }
//        }
//        return message;
//    }

    @Override
    public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler,
                             @Nullable Exception ex) {
        StompHeaderAccessor inAccessor = StompHeaderAccessor.wrap(message);
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

        channel.send(outMessage);
    }




}
