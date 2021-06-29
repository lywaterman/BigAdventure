package com.bad.bigad.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public enum WsSessionManager {
    instance;

    private ConcurrentHashMap<Long, WebSocketSession> sessionPool;
    {
        sessionPool = new ConcurrentHashMap<>();
    }

    public void addSession(Long playerId, WebSocketSession session) {

    }
}
