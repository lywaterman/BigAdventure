package com.bad.bigad.game;

import com.bad.bigad.entity.Player;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    protected int roomType;
    //玩家列表
    transient protected Map<Long, Player> playerList = new ConcurrentHashMap<>();

    public void onEnter(Player player) {
        playerList.put(player.getId(), player);
        player.setRoom(this);
    }

    public void onLeave(Player player) {
        playerList.remove(player.getId(), player);
        player.setRoom(null);
    }

    public void broadcastMessage(String msg) {

    }

    public void onMessage(Player player, WebSocketSession session, String message) {

    }
}
