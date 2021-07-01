package com.bad.bigad.service.impl;

import com.bad.bigad.model.ChatMessage;
import com.bad.bigad.service.ChatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImp implements ChatService {
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    private Gson gson;

    {
        gson = new Gson();
    }

    @Override
    public boolean sendMsg(String msg) {
        ChatMessage message = gson.fromJson(msg, ChatMessage.class);
        return sendMsg(message);
    }

    @Override
    public boolean sendMsg(ChatMessage msg) {
        try {
            if (msg.getToUser()) {
                simpMessageSendingOperations.convertAndSendToUser(
                        msg.getTo(),
                        "/topic/msg",
                        msg);
            } else {
                simpMessageSendingOperations.convertAndSend("/topic/" + msg.getTo(), msg);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
