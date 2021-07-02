package com.bad.bigad.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlayerOnlineStatus implements Serializable {
    public PlayerOnlineStatus(int serverId) {
        this.serverId = serverId;
    }
    public PlayerOnlineStatus() {

    }
    private int serverId;   //玩家在那个服务器     -1代表不在线
}
