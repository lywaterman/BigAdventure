package com.bad.bigad.service.impl;

import com.bad.bigad.entity.Player;
import com.bad.bigad.mapper.PlayerMapper;
import com.bad.bigad.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class PlayerServiceImp implements PlayerService {
    @Autowired
    private PlayerMapper playerMapper;

    @Override
    public Player findById(int id) {
        return playerMapper.findById(id);
    }

    @Override
    public Player findByWxName(String wx_name) {
        return playerMapper.findByWxName(wx_name);
    }

    @Override
    public Player CreateNew(String wx_name, String wx_nick_name) {
        Player p = new Player();
        p.setWx_name(wx_name);
        p.setWx_nick_name(wx_nick_name);
        playerMapper.insertPlayer(p);
        return p;
    }
}
