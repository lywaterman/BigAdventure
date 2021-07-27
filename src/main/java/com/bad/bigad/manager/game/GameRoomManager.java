package com.bad.bigad.manager.game;

import com.bad.bigad.entity.game.GameRoom;
import com.bad.bigad.game.LobbyRoom;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class GameRoomManager {
    @Autowired
    private LobbyRoom lobbyRoom;

    private Map<Long, GameRoom> gameRoomMap = new HashMap<>();

    public GameRoom getRoom(long id) {
        return gameRoomMap.get(id);
    }

    public void putRoom(GameRoom gameRoom) {
        gameRoomMap.put(gameRoom.getId(), gameRoom);
    }
}
