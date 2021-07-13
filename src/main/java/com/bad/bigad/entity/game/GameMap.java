package com.bad.bigad.entity.game;

import com.bad.bigad.manager.ScriptManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class GameMap {
    private int id;
    private int status;
    private List<List<Grid>> gridList = new ArrayList<>();
    private HashMap<String, Grid> varGrid;

}
