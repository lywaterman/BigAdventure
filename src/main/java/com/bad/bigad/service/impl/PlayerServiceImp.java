package com.bad.bigad.service.impl;

import com.bad.bigad.entity.Player;
import com.bad.bigad.manager.WsSessionManager;
import com.bad.bigad.mapper.PlayerMapper;
import com.bad.bigad.service.PlayerService;
import com.bad.bigad.service.game.GameReelService;
import com.bad.bigad.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

@Slf4j
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

    @Autowired
    GameReelService gameReelService;

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
        Player player = players.get(id);
        if (player != null) {
            player.setReelList(gameReelService.getGameReelByOwnerId(id));
        }
        return player;
    }

    @Override
    public  boolean kickPlayer(Long id, String message) {
        log.info("踢掉玩家:"+id);
        wsSessionManager.sendMessage(id, message);

        wsSessionManager.close(id);

        return true;
    }
}
