package com.bad.bigad.controller;

import com.bad.bigad.service.PlayerService;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
public class LoginController {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    PlayerService playerService;

    @RequestMapping("/login")
    public Object login() {
        RMap<Object,Object> m = redissonClient.getMap("player:001");
        m.put("name", "ly");
        m.put("age", 18);
        return "Hello world";
    }

    @RequestMapping("/test")
    public Object test() {
//        RMap<Object, Object> m = redissonClient.getMap("player:001");
//        return null;
        return playerService.findById(1).getWx_name();
    }
}
