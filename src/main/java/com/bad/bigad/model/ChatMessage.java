package com.bad.bigad.model;

import lombok.Data;

@Data
public class ChatMessage {
    private String world;
    private String type;
    private String content;
    private String sender;
}
