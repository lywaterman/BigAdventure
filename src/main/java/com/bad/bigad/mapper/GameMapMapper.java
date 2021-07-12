package com.bad.bigad.mapper;

import com.bad.bigad.entity.game.GameMap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GameMapMapper {
    public GameMap getGameMapById(int id);
    public List<GameMap> getAllGameMap();
}
