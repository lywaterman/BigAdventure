package com.bad.bigad.mapper;

import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.entity.game.GameRoom;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GameRoomMapper {
    public GameRoom getGameRoomById(long id);
    public void updateGameRoom(GameRoom gameRoom);
    public void newGameRoom(GameRoom gameRoom);
}
