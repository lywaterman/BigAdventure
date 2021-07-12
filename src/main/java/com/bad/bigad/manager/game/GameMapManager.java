package com.bad.bigad.manager.game;

import com.bad.bigad.entity.game.GameMap;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameMapManager {
    private ConcurrentHashMap<Integer, GameMap> gameMaps;
    {
        gameMaps = new ConcurrentHashMap<Integer, GameMap>();
    }


}
