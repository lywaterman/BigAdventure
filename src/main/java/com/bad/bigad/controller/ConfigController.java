package com.bad.bigad.controller;

import com.bad.bigad.config.ClusterConfig;
import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.entity.game.GameReel;
import com.bad.bigad.entity.game.GameRoom;
import com.bad.bigad.manager.game.GameMapManager;
import com.bad.bigad.service.game.GameMapService;
import com.bad.bigad.service.game.GameReelService;
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

    @Autowired
    GameReelService gameReelService;

    @RequestMapping("/config")
    public Map getServerList() {
        return clusterConfig.getServerNodes();
    }

    @RequestMapping("/testGameRoom")
    public GameRoom testGameRoom(@RequestParam int id) {
        gameRoomService.init();
        return gameRoomService.getGameRoomById(1);
    }

    @RequestMapping("testGameReel")
    public Map<Long,GameReel> testGameReel(@RequestParam int temp_id) {
        GameReel gameReel = gameReelService.newGameReel(1, 1);

        Map<Long,GameReel> gameReelList  = gameReelService.getGameReelByOwnerId(gameReel.getOwnerId());
        return gameReelList;
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
