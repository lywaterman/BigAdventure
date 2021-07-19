package com.bad.bigad.service.impl;

import com.bad.bigad.entity.Player;
import com.bad.bigad.manager.PlayerManager;
import com.bad.bigad.manager.WsSessionManager;
import com.bad.bigad.mapper.PlayerMapper;
import com.bad.bigad.service.PlayerService;
import com.bad.bigad.util.Util;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class PlayerServiceImp implements PlayerService {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private PlayerMapper playerMapper;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    WsSessionManager wsSessionManager;

    @Override
    public Player findById(long id) {
        return playerMapper.findById(id);
    }

    @Override
    public Player findByWxName(String wx_name) {
        return playerMapper.findByWxName(wx_name);
    }

    @Override
    public Player newPlayer(String wx_name, String wx_nick_name) {
        Player p = new Player();
        p.setId(Util.instance.getSnowId());
        p.setWx_name(wx_name);
        p.setWx_nick_name(wx_nick_name);
        playerMapper.insertPlayer(p);
        return p;
    }

    @Override
    public Player GetPlayerFromCache(Long id) {
        RMap<Long, Player> players = redissonClient.getMap("players");
        return players.get(id);
    }

    @Override
    public  boolean kickPlayer(Long id, String message) {
        wsSessionManager.sendMessage(id, message);

        wsSessionManager.close(id);

        return true;
    }
}
