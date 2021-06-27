package com.bad.bigad.service;

import com.bad.bigad.model.ChatMessage;

public interface ChatService {
    public boolean sendMsg(String msg);
    public boolean sendMsg(ChatMessage msg);
}
