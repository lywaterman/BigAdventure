package com.bad.bigad.service.impl;

import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.entity.game.Grid;
import com.bad.bigad.manager.ScriptManager;
import com.bad.bigad.manager.game.GameMapManager;
import com.bad.bigad.mapper.GameMapMapper;
import com.bad.bigad.service.game.GameMapService;
import com.bad.bigad.util.Util;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class GameMapServiceImp implements GameMapService {

    @Autowired
    private GameMapMapper gameMapMapper;

    @Autowired
    private ScriptManager scriptManager;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GameMapManager gameMapManager;

    public boolean initFromScript(GameMap map) {
        ScriptObjectMirror gameMap = (ScriptObjectMirror) scriptManager.callJs(
                "getGameMapConfig",
                map.getTempId());

        int x = (int)gameMap.get("x");
        int y = (int)gameMap.get("y");

        for (int i=0; i<x; i++) {
            List<Grid> grids = new ArrayList<>();
            for (int j=0; j<y; j++) {
                grids.add(new Grid());
            }
            map.getGridList().add(grids);
        }

        Iterator iterator = map.getVarGrid().entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();

            String[] pos = entry.getKey().toString().split("|");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[2]);

            map.getGridList().get(posX).add(posY,(Grid) entry.getValue());
        }

        return true;
    }

    @Override
    public GameMap getGameMapById(long id) {
        GameMap gameMap = gameMapManager.getGameMap(id);

        if (gameMap != null) {
            return gameMap;
        }

        RMap<Long, GameMap> maps = redissonClient.getMap("gamemaps");
        gameMap = maps.get(id);

        //cache没有，取db找
        if (gameMap == null) {
            gameMap = gameMapMapper.getGameMapById(id);
        }

        if (gameMap != null) {
            initFromScript(gameMap);

            gameMapManager.putGameMap(gameMap);
        }

        return gameMap;
    }

    @Override
    public GameMap newGameMap(int tempId) {
        RMap<Long, GameMap> maps = redissonClient.getMap("gamemaps");

        GameMap gameMap = new GameMap();
        gameMap.setTempId(tempId);
        gameMap.setId(Util.instance.getMapSnowId());

        initFromScript(gameMap);

        //先写db
        gameMapMapper.newGameMap(gameMap);
        //序列化，放入cache
        maps.put(gameMap.getId(), gameMap);

        gameMapManager.putGameMap(gameMap);

        return gameMap;
    }


    @Override
    public List<GameMap> getAllGameMap() {
        return null;
    }

    //更新cache和db
    @Override
    public void updateGameMap(GameMap gameMap) {
        gameMapManager.putGameMap(gameMap);

        RMap<Long, GameMap> maps = redissonClient.getMap("gamemaps");
        maps.put(gameMap.getId(),gameMap);

        gameMapMapper.updateGameMap(gameMap);
    }
}
