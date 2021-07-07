package com.bad.bigad.model;

import lombok.Data;

@Data
public class ChatMessage {
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        ANOTHER
    }
    private String to; //接收者
    private MessageType type;
    private Boolean toUser;
    private String content;
    private String sender;   //发送者
    private String sender_id;
}
