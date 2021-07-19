package com.bad.bigad.manager.game;

import com.bad.bigad.entity.game.GameRoom;
import com.bad.bigad.game.LobbyRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GameRoomManager {
    @Autowired
    private LobbyRoom room;

    private Map<Long, GameRoom> gameRoomMap = new HashMap<>();

    public void init() {
        //初始化房间放这里

    }

    public GameRoom getRoom(long id) {
        return gameRoomMap.get(id);
    }

    public void putRoom(GameRoom gameRoom) {
        gameRoomMap.put(gameRoom.getId(), gameRoom);
    }
}
