package com.bad.bigad.mapper;

import com.bad.bigad.entity.game.GameMap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GameRoomMapper {
    public GameMap getGameRoomById(long id);
    public void updateGameRoom(GameMap gameMap);
    public void newGameRoom(GameMap gameMap);
}
