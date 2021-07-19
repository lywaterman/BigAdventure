package com.bad.bigad.service.game;


import com.bad.bigad.entity.game.GameMap;

import java.util.List;

public interface GameMapService {
    public GameMap getGameMapById(long id);
    public GameMap newGameMap(int tempId);
    public List<GameMap> getAllGameMap();
    public void updateGameMap(GameMap gameMap);
}
