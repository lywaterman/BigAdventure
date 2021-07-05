package com.bad.bigad.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public enum BridgeForJs {
    instance;

    public Logger log = LoggerFactory.getLogger(BridgeForJs.class);

    public boolean sendMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
