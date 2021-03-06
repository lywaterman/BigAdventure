package com.bad.bigad.controller;

import com.bad.bigad.config.UserPrincipal;
import com.bad.bigad.util.Util;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.bad.bigad.model.ChatMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Controller
public class ChatController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MessageMapping("/chat.sendMsg")
    public void sendMsg(@Payload ChatMessage chatMessage, Principal principal) {
        String nick_name = ((UserPrincipal)principal).getNick_name();
        chatMessage.setSender(nick_name);
        chatMessage.setSender_id(((UserPrincipal)principal).getName());
        rabbitTemplate.convertAndSend(
                "topicWebSocketExchange",
                "topic.public",
                Util.gson.toJson(chatMessage));

       // rabbitTemplate.convertAndSend("chatFanoutExchange", "",  Util.gson.toJson(chatMessage));
    }
}
