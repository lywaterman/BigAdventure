package com.bad.bigad.manager.game;

import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.manager.ScriptManager;
import com.bad.bigad.service.game.GameMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

//重要，设计先找GameMapManager，没有就找service，service来处理cache和db
@Component
public class GameMapManager {
    @Autowired
    ScriptManager scriptManager;

    @Autowired
    GameMapService gameMapService;

    private ConcurrentHashMap<Long, GameMap> gameMaps;
    {
        gameMaps = new ConcurrentHashMap<Long, GameMap>();
    }

    public GameMap getGameMap(long id) {
        GameMap gameMap = gameMaps.get(id);

        return gameMap;
    }

    public void putGameMap(GameMap gameMap) {
        gameMaps.put(gameMap.getId(), gameMap);
    }
}
