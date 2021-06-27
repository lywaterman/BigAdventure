package com.bad.bigad.config;

import com.bad.bigad.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;

@Component
public class HeaderParamInterceptor implements ChannelInterceptor {
    @Value("${jwt_key}")
    String jwtKey;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                Object token = ((Map) raw).get("token");

                if (token instanceof LinkedList) {
                    Map<String, Object> claims = Util.instance.parseToken(
                            ((LinkedList) token).get(0).toString(),
                            jwtKey);
                    accessor.setUser(new UserPrincipal((String) claims.get("wx_name")));
                }
            }
        }
        return message;
    }
}
