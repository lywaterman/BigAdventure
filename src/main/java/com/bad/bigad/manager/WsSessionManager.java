package com.bad.bigad.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public enum WsSessionManager {
    instance;

    private ConcurrentHashMap<Long, WebSocketSession> sessionPool;
    {
        sessionPool = new ConcurrentHashMap<>();
    }

    public void add(Long playerId, WebSocketSession session) {
        sessionPool.put(playerId, session);
    }

    public void remove(Long playerId) {
        sessionPool.remove(playerId);
    }


    public WebSocketSession get(Long playerId) {
        return sessionPool.get(playerId);
    }

    public void removeAddClose(Long playerId) {
        WebSocketSession session = sessionPool.remove(playerId);
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean sendMessage(Long playerId, String message) {
        WebSocketSession webSocketSession = sessionPool.get(playerId);
        if (webSocketSession != null) {
            try {
                webSocketSession.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

}
