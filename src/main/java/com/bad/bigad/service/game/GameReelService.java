package com.bad.bigad.service.game;

import com.bad.bigad.entity.game.GameReel;

import java.util.List;
import java.util.Map;

public interface GameReelService {
    public Map<Long, GameReel> getGameReelByOwnerId(long ownerId);
    public void updateGameReel(GameReel gameReel);
    public GameReel newGameReel(long ownerId, int tempId);
}
