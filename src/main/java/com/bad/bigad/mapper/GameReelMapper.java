package com.bad.bigad.mapper;

import com.bad.bigad.entity.game.GameReel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameReelMapper {
    public GameReel getGameReelByOwnerId(@Param("ownerId") long ownerId);
    public void addGameReelCount(@Param("id") long id,@Param("count") int count);
    public void newGameReel(GameReel gameReel);
}
