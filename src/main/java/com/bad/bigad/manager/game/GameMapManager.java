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

    private ConcurrentHashMap<Long, GameMap> gameMaps;
    {
        gameMaps = new ConcurrentHashMap<Long, GameMap>();
    }

    public GameMap getGameMap(long id) {
        //内存没有交给service去找
        GameMap gameMap = gameMaps.get(id);
        if (gameMap == null) {
            gameMapService.getGameMapById(id);
        }

        return gameMap;
    }

    //创建地图按照模板
    public GameMap newGameMap(int tempId) {
        GameMap gameMap = gameMapService.createGameMap(tempId);
        return gameMap;
    }

    public boolean init() {
        //加载所有的地图进内存
        scriptManager.callJs("initGameMapManager", this);
        return true;
    }
}
