package com.bad.bigad.service.impl;

import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.entity.game.GameReel;
import com.bad.bigad.mapper.GameReelMapper;
import com.bad.bigad.service.game.GameReelService;
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
        RMap<Long, Map<Long, GameReel>> gameReels = redissonClient.getMap("gamereels");
        Map<Long, GameReel> gameReelMap = gameReels.get(ownerId);

        if (gameReelMap != null) {
            return gameReelMap;
        }
        List<GameReel> gameReelList = gameReelMapper.getGameReelByOwnerId(ownerId);

        gameReelMap = new HashMap<>();
        for (GameReel gameReel : gameReelList) {
            gameReelMap.put(gameReel.getId(), gameReel);
        }
        return gameReelMap;
    }

    @Override
    public void addGameReelCount(long id, int count) {
        gameReelMapper.addGameReelCount(id, count);
    }

    @Override
    public GameReel newGameReel(long ownerId, int tempId) {
        GameReel gameReel = new GameReel();

        return null;
    }
}
