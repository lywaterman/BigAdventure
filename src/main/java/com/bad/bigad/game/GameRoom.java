package com.bad.bigad.game;

import com.bad.bigad.entity.Player;
import com.bad.bigad.entity.game.GameMap;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//房间与地图绑定,房间会有地图
@Data
public class GameRoom {
    //当前的map
    private GameMap curMap;
    //玩家列表
    private Map<Long, Player> playerList = new ConcurrentHashMap<>();

    public void onEnter(Player player) {
        playerList.put(player.getId(), player);
    }

    public void onLeave(Player player) {
        playerList.remove(player.getId(), player);
    }

    public void onMessage(Player player, WebSocketSession session, String message) {
        
    }
}
