package com.bad.bigad.service.impl;

import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.entity.game.GameReel;
import com.bad.bigad.mapper.GameReelMapper;
import com.bad.bigad.service.game.GameReelService;
import com.bad.bigad.util.Util;
import org.redisson.api.RListMultimap;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameReelServiceImp implements GameReelService {
    @Autowired
    GameReelMapper gameReelMapper;

    @Autowired
    RedissonClient redissonClient;

    public void initFromScript(GameReel gameReel) {

    }

    @Override
    public Map<Long, GameReel> getGameReelByOwnerId(long ownerId) {
        //保存玩家身上的卷轴
        RMap<Long, GameReel> gameReels = redissonClient.getMap(String.valueOf(ownerId));
        Map<Long, GameReel> gameReelMap;
        if (!gameReels.isEmpty()) {
            //不是空，返回给玩家
            gameReelMap = gameReels.readAllMap();
        } else {
            List<GameReel> gameReelList = gameReelMapper.getGameReelByOwnerId(ownerId);

            gameReelMap = new HashMap<>();
            for (GameReel gameReel : gameReelList) {
                gameReelMap.put(gameReel.getId(), gameReel);
                gameReels.put(gameReel.getId(), gameReel);
            }
        }
        return gameReelMap;
    }

    @Override
    public void updateGameReel(GameReel gameReel) {
        RMap<Long, GameReel> gameReels = redissonClient.getMap(String.valueOf(gameReel.getOwnerId()));
        gameReels.put(gameReel.getId(), gameReel);

        gameReelMapper.updateGameReel(gameReel);
    }

    @Override
    public GameReel newGameReel(long ownerId, int tempId) {
        GameReel gameReel = new GameReel();

        gameReel.setOwnerId(ownerId);
        gameReel.setTempId(tempId);
        gameReel.setId(Util.instance.getReelSnowId());

        initFromScript(gameReel);

        RMap<Long, GameReel> gameReels = redissonClient.getMap(String.valueOf(gameReel.getOwnerId()));
        gameReels.put(gameReel.getId(), gameReel);

        gameReelMapper.newGameReel(gameReel);

        return gameReel;
    }
}
