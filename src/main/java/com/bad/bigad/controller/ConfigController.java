package com.bad.bigad.controller;

import com.bad.bigad.config.ClusterConfig;
import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.manager.game.GameMapManager;
import com.bad.bigad.service.game.GameMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RefreshScope
public class ConfigController {
    @Autowired
    ClusterConfig clusterConfig;

    @Autowired
    GameMapService gameMapService;

    @Autowired
    GameMapManager gameMapManager;

    @RequestMapping("/config")
    public Map getServerList() {
        return clusterConfig.getServerNodes();
    }

    @RequestMapping("/testGameMap")
    public GameMap testGameMap() {
        gameMapManager.init();
        GameMap gameMap = gameMapManager.getGameMap(1);

        return gameMap;
    }

    @RequestMapping("/updateGameMap")
    public int updateGameMap() {
        GameMap gameMap = gameMapManager.getGameMap(1);
        gameMap.setStatus(101);
        gameMapService.updateGameMap(gameMap);
        return 1;
    }
}
