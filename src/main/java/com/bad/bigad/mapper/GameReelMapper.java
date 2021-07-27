package com.bad.bigad.mapper;

import com.bad.bigad.entity.game.GameReel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameReelMapper {
    public List<GameReel> getGameReelByOwnerId(@Param("ownerId") long ownerId);
    public void updateGameReel(GameReel gameReel);
    public void newGameReel(GameReel gameReel);
}
