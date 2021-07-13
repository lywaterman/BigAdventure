package com.bad.bigad.manager.game;

import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.manager.ScriptManager;
import com.bad.bigad.service.game.GameMapService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
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

    private ConcurrentHashMap<Integer, GameMap> gameMaps;
    {
        gameMaps = new ConcurrentHashMap<Integer, GameMap>();
    }

    public GameMap getGameMap(int id) {
        GameMap gameMap = gameMaps.get(id);
        if (gameMap == null) {
            gameMap =  gameMapService.getGameMapById(id);
            gameMaps.put(id, gameMap);
        }

        return gameMap;
    }

    public boolean init() {
        //加载所有的地图进内存
        scriptManager.callJs("initGameMapManager", this);
        return true;
    }
}
