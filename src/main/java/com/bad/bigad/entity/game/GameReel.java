package com.bad.bigad.entity.game;

import lombok.Data;

import java.io.Serializable;

@Data
public class GameReel implements Serializable {
    private long id;
    private int tempId;
    private int maxFrag;
    private int curFrag;
    private long ownerId;
}
