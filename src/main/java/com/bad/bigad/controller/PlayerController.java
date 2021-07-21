package com.bad.bigad.controller;

import com.bad.bigad.model.PlayerOnlineStatus;
import com.bad.bigad.service.PlayerService;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("/kickPlayer")
    public boolean kickPlayer(@RequestParam Long id) {
        return playerService.kickPlayer(id, "您在其他地方登陆了");
    }

    @RequestMapping("/queryPlayerStatus")
    public Map<Long, PlayerOnlineStatus> queryPlayerStatus() {
        //RMapCache<Long, PlayerOnlineStatus> map = redissonClient.getMapCache("online_status");
        RMapCache<Long, PlayerOnlineStatus> map = redissonClient.getMapCache("online_status");
        //map.expire(10, TimeUnit.SECONDS);
        return map;
    }
}
