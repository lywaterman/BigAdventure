package com.bad.bigad.service.impl;

import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.entity.game.GameRoom;
import com.bad.bigad.manager.ScriptManager;
import com.bad.bigad.manager.game.GameRoomManager;
import com.bad.bigad.mapper.GameRoomMapper;
import com.bad.bigad.service.game.GameMapService;
import com.bad.bigad.service.game.GameRoomService;
import com.bad.bigad.util.Util;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameRoomServiceImp implements GameRoomService {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    GameRoomMapper gameRoomMapper;

    @Autowired
    GameMapService gameMapService;

    @Autowired
    GameRoomManager gameRoomManager;

    @Autowired
    ScriptManager scriptManager;

    @Override
    public GameRoom getGameRoomById(long id) {
        //内存找
        GameRoom gameRoom = gameRoomManager.getRoom(id);
        if (gameRoom != null) {
            return gameRoom;
        }

        //cache找
        RMap<Long, GameRoom> maps = redissonClient.getMap("gamerooms");
        gameRoom = maps.get(id);

        //cache没有，取db找
        if (gameRoom == null) {
            gameRoom = gameRoomMapper.getGameRoomById(id);
        }

        if (gameRoom != null) {
            gameRoom.setCurMap(
                    gameMapService.getGameMapById(gameRoom.getMapId())
            );

            gameRoomManager.putRoom(gameRoom);
        }

        return gameRoom;
    }
    @Override
    public GameRoom newGameRoom(long id, int mapTempId, int roomType) {
        RMap<Long, GameRoom> gamerooms = redissonClient.getMap("gamerooms");

        GameMap gameMap = gameMapService.newGameMap(mapTempId);
        GameRoom gameRoom = new GameRoom(gameMap.getId(), roomType);

        if (id == 0) {
            gameRoom.setId(Util.instance.getRoomSnowId());
            gameRoomMapper.newGameRoom(gameRoom);
        } else {
            gameRoom.setId(id);
            gameRoomMapper.updateGameRoom(gameRoom);
        }

        //序列化，放入cache
        gamerooms.put(gameRoom.getId(), gameRoom);
        gameRoomManager.putRoom(gameRoom);

        return gameRoom;
    }

    @Override
    public List<GameRoom> getAllGameRoom() {
        return null;
    }

    @Override
    public void updateGameRoom(GameRoom gameRoom) {
        gameRoomManager.putRoom(gameRoom);

        RMap<Long, GameRoom> maps = redissonClient.getMap("gamerooms");
        maps.put(gameRoom.getId(),gameRoom);

        gameRoomMapper.updateGameRoom(gameRoom);
    }

    @Override
    public void init() {
        scriptManager.callJs("initGameRoomService");
    }
}
