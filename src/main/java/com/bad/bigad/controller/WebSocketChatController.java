package com.bad.bigad.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.bad.bigad.model.ChatMessage;

@Controller
public class WebSocketChatController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @MessageMapping("/chat.sendM2M")
    @SendTo("/topic/javainuse")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("chat.sendM2S")
    public void sendMessage2S(@Payload ChatMessage chatMessage) {

    }

    @MessageMapping("/chat.newUser")
    @SendTo("/topic/javainuse")
    public ChatMessage newUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
