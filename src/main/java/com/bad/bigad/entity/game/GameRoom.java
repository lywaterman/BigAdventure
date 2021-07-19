package com.bad.bigad.entity.game;

import com.bad.bigad.entity.Player;
import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.game.Room;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//房间与地图绑定,房间会有地图
@Data
public class GameRoom extends Room implements Serializable {
    //当前的map
    protected long mapId;
    transient protected GameMap curMap;

    public GameRoom(long mapId, int roomType) {
        this.mapId = mapId;
        this.roomType = roomType;
    }
}
