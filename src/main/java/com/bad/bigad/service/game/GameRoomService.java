package com.bad.bigad.service.game;

import com.bad.bigad.entity.game.GameRoom;

import java.util.List;

public interface GameRoomService {
    public GameRoom getGameRoomById(long id);
    public GameRoom newGameRoom(long id, int mapTempId, int roomType);
    public List<GameRoom> getAllGameRoom();
    public void updateGameRoom(GameRoom gameMap);
    public void init();
}
