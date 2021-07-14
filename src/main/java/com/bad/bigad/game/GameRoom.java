package com.bad.bigad.game;

import com.bad.bigad.entity.Player;
import com.bad.bigad.entity.game.GameMap;
import lombok.Data;

import java.util.Map;

//房间与地图绑定,房间会有地图
@Data
public class GameRoom {
    //当前的map
    private GameMap curMap;
    //玩家列表
    private Map<Long, Player> playerList;
}
