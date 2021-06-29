package com.bad.bigad.manager;

import com.bad.bigad.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public enum PlayerManager {
    instance;

    private ConcurrentHashMap<Long, Player> players;
    {
        players = new ConcurrentHashMap<>();
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
