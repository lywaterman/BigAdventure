package com.bad.bigad.config;

import com.bad.bigad.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
public class TokenCheckInterceptor implements HandshakeInterceptor {
    @Value("${jwt_key}")
    String jwtKey;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
        String token = httpServletRequest.getParameter("token");

        if (token == null) {
            log.info("token是空的");
            return false;
        }

        Map<String, Object> claims = Util.instance.parseToken(token, jwtKey);

        if(claims == null) {
            log.info("websocket链接认证失败");
            return false;
        }
        log.info("websocket链接认证成功");
        String userName = (String) claims.get("wx_name");
        //保存认证用户
        attributes.put("user", new Principal() {
            @Override
            public String getName() {
                return userName;
            }
        });

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
