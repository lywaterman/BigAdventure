package com.bad.bigad.service.game;


import com.bad.bigad.entity.game.GameMap;

import java.util.List;

public interface GameMapService {
    public GameMap getGameMapById(int id);
    public List<GameMap> getAllGameMap();
    public void updateGameMap(GameMap gameMap);
}
