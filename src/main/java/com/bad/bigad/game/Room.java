package com.bad.bigad.game;

import com.bad.bigad.entity.Player;
import com.bad.bigad.manager.ScriptManager;
import com.bad.bigad.util.BridgeForJs;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Component
public class Room {
    protected long id;
    protected int roomType;

    @Autowired
    private ScriptManager scriptManager;
    //玩家列表
    @JsonIgnore
    transient protected Map<Long, Player> playerList = new ConcurrentHashMap<>();

    public void onEnter(Player player) {
        playerList.put(player.getId(), player);
        player.setRoom(this);

        scriptManager.callJs("onEnter", player, this);
    }

    public void onLeave(Player player, String desc) {
        playerList.remove(player.getId(), player);
        player.setRoom(null);

        scriptManager.callJs("onLeave", player, this);
    }

    public void broadcastMessageExcept(String msg, Player player) {
        Iterator<Map.Entry<Long, Player>> iter = playerList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long, Player> entry = iter.next();

            if (player != null && entry.getKey() == player.getId()) {
                continue;
            }

            BridgeForJs.instance.sendMessage(entry.getValue(), msg);
        }
    }

    public void onMessage(Player player, String message) {
        scriptManager.callJs("onMessage", message, player, this);
    }
}
