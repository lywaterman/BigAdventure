package com.bad.bigad.controller;

import com.bad.bigad.config.ClusterConfig;
import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.entity.game.GameRoom;
import com.bad.bigad.manager.game.GameMapManager;
import com.bad.bigad.service.game.GameMapService;
import com.bad.bigad.service.game.GameRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    GameRoomService gameRoomService;

    @Autowired
    GameMapManager gameMapManager;

    @RequestMapping("/config")
    public Map getServerList() {
        return clusterConfig.getServerNodes();
    }

    @RequestMapping("/testGameRoom")
    public GameRoom testGameRoom(@RequestParam int id) {
        return gameRoomService.newGameRoom(1, 1, 1);
    }

    @RequestMapping("/testGameMap")
    public GameMap testGameMap(@RequestParam int id) {
        return null;
    }

    @RequestMapping("/updateGameMap")
    public int updateGameMap(@RequestParam int id) {
        return 1;
    }
}
