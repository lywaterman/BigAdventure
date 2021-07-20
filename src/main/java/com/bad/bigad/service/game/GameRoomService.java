package com.bad.bigad.service.game;

import com.bad.bigad.entity.game.GameRoom;

import java.util.List;

public interface GameRoomService {
    public GameRoom getGameRoomById(long id);
    public GameRoom newPublicGameRoom(long id, int roomType, int mapTempId);
    public GameRoom newGameRoom(int roomType, int mapTempId);
    public List<GameRoom> getAllGameRoom();
    public void updateGameRoom(GameRoom gameMap);
    public void init();
}
