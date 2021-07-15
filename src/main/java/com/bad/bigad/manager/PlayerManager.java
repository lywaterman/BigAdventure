package com.bad.bigad.manager;

import com.bad.bigad.entity.Player;
import com.bad.bigad.model.PlayerOnlineStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

public enum PlayerManager {
    instance;

    private ConcurrentHashMap<Long, Player> players;
    {
        players = new ConcurrentHashMap<>();
    }

    private ConcurrentHashMap<Long, PlayerOnlineStatus> status_map;
    {
        status_map = new ConcurrentHashMap<>();
    }

    //Session Link Player
    private ConcurrentHashMap<WebSocketSession, Player> playerSessionLink;
    {
        playerSessionLink = new ConcurrentHashMap<>();
    }


    public ConcurrentHashMap<Long, PlayerOnlineStatus> getStatusMap() {
        return status_map;
    }

    public void add(Long playerId, Player player, PlayerOnlineStatus status) {
        players.put(playerId, player);
        status_map.put(playerId, status);
    }

    public Player remove(Long playerId) {
        status_map.remove(playerId);
        return players.remove(playerId);
    }

    public Player get(Long playerId) {
        return players.get(playerId);
    }
}
