package com.bad.bigad.service.impl;

import com.bad.bigad.entity.Player;
import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.entity.game.Grid;
import com.bad.bigad.manager.ScriptManager;
import com.bad.bigad.mapper.GameMapMapper;
import com.bad.bigad.service.game.GameMapService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private GameMapMapper playerMapper;

    @Autowired
    private ScriptManager scriptManager;

    @Autowired
    private RedissonClient redissonClient;

    public boolean initFromScript(GameMap map) {
        ScriptObjectMirror gameMap = (ScriptObjectMirror) scriptManager.callJs(
                "getGameMapConfig",
                map.getId());

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
            Map.Entry mentry = (Map.Entry)iterator.next();

            String[] pos = mentry.getKey().toString().split("|");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[2]);

            map.getGridList().get(posX).add(posY,(Grid) mentry.getValue());
        }

        return true;
    }

    @Override
    public GameMap getGameMapById(int id) {
        RMap<Integer, GameMap> maps = redissonClient.getMap("gamemaps");

        GameMap gameMap = maps.get(id);

        if (gameMap == null) {
            gameMap = playerMapper.getGameMapById(id);
            initFromScript(gameMap);

            maps.put(id, gameMap);

        }

        return gameMap;
    }

    @Override
    public List<GameMap> getAllGameMap() {
        return null;
    }
}
