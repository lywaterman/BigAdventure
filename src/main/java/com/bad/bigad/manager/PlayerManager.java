package com.bad.bigad.manager;

import com.bad.bigad.entity.Player;
import com.bad.bigad.model.PlayerOnlineStatus;

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


    public ConcurrentHashMap<Long, PlayerOnlineStatus> getStatusMap() {
        return status_map;
    }

    public void add(Long playerId, Player player) {
        players.put(playerId, player);
    }

    public void remove(Long playerId) {
        players.remove(playerId);
    }

    public Player get(Long playerId) {
        return players.get(playerId);
    }
}
