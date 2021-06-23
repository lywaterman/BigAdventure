package com.bad.bigad.service;

import com.bad.bigad.entity.Player;

public interface PlayerService {
    public Player findById(int id);
    public Player findByWxName(String wx_name);
    public Player CreateNew(String wx_name, String wx_nick_name);
}
