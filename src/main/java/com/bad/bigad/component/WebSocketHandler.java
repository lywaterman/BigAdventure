package com.bad.bigad.component;

import com.bad.bigad.manager.WsSessionManager;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
//        for(WebSocketSession webSocketSession : sessions) {
//            Map value = new Gson().fromJson(message.getPayload(), Map.class);
//            webSocketSession.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));
//        }
        String userName = (String) session.getAttributes().get("id");
        session.sendMessage(new TextMessage("Hello " + userName));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //the messages will be broadcasted to all users.
        WsSessionManager.instance.add(
                Long.parseLong(session.getPrincipal().getName()),
                session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        WsSessionManager.instance.remove(
                Long.parseLong(session.getPrincipal().getName())
        );
    }
}