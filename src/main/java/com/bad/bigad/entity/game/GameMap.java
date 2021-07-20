package com.bad.bigad.entity.game;

import com.bad.bigad.manager.ScriptManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//地图要有id和temple id，
@Data
public class GameMap implements Serializable {
    private long id;
    private int status;
    private int tempId;
    //这个不用存db，也不用存cache
    @JsonIgnore
    transient private List<List<Grid>> gridList = new ArrayList<>();

    private HashMap<String, Grid> varGrid = new HashMap<>();

    public GameMap(int tempId) {
        this.tempId = tempId;
    }

    public List<List<Grid>> getGridList() {
        if (gridList == null) {
            gridList = new ArrayList<>();
        }

        return gridList;
    }
}
