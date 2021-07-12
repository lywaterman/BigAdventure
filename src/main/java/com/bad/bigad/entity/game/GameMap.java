package com.bad.bigad.entity.game;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameMap {
    private int id;
    private int status;
    private List<List<Grid>> gridList = new ArrayList<>();

    public GameMap(int x, int y) {
        for (int i=0; i<y; i++) {
            gridList.add(new ArrayList<>(x));
        }
    }
}
