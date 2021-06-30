package com.bad.bigad.service;

import com.bad.bigad.entity.Player;

public interface PlayerService {
    public Player findById(long id);
    public Player findByWxName(String wx_name);
    public Player CreateNew(String wx_name, String wx_nick_name);
    public Player GetPlayerFromCache(Long id);
    public boolean KickoutPlayer(Long id);
}
