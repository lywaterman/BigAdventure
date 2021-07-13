package com.bad.bigad.controller;

import com.bad.bigad.config.ClusterConfig;
import com.bad.bigad.entity.game.GameMap;
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

    @RequestMapping("/config")
    public Map getServerList() {
        return clusterConfig.getServerNodes();
    }

    @RequestMapping("/testGameMap")
    public GameMap testGameMap() {
        return gameMapService.getGameMapById(1);
    }
}
