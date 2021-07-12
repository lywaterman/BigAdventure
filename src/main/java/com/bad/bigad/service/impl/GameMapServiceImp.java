package com.bad.bigad.service.impl;

import com.bad.bigad.entity.game.GameMap;
import com.bad.bigad.mapper.GameMapMapper;
import com.bad.bigad.service.game.GameMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameMapServiceImp implements GameMapService {

    @Autowired
    private GameMapMapper playerMapper;

    @Override
    public GameMap getGameMapById(int id) {
        GameMap map = playerMapper.getGameMapById(id);
        return map;
    }

    @Override
    public List<GameMap> getAllGameMap() {
        return null;
    }
}
