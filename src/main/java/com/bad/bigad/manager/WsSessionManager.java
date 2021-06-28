package com.bad.bigad.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public enum WsSessionManager {
    instance;

    private ConcurrentHashMap<String, WebSocketSession> sessionPool;
    {
        sessionPool = new ConcurrentHashMap<>();
    }

}
