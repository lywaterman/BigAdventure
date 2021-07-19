package com.bad.bigad.service.impl;

import com.bad.bigad.entity.game.GameRoom;
import com.bad.bigad.manager.game.GameRoomManager;
import com.bad.bigad.mapper.GameRoomMapper;
import com.bad.bigad.service.game.GameMapService;
import com.bad.bigad.service.game.GameRoomService;
import com.bad.bigad.util.Util;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GameRoomServiceImp implements GameRoomService {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    GameRoomMapper gameRoomMapper;

    @Autowired
    GameMapService gameMapService;

    @Autowired
    GameRoomManager gameRoomManager;

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
    public GameRoom newGameRoom(int mapId, int roomType) {
        RMap<Long, GameRoom> maps = redissonClient.getMap("gamerooms");

        GameRoom gameRoom = new GameRoom(mapId, roomType);
        gameRoom.setId(Util.instance.getRoomSnowId());

        //先写db
        gameRoomMapper.newGameRoom(gameRoom);
        //序列化，放入cache
        maps.put(gameRoom.getId(), gameRoom);

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
}
