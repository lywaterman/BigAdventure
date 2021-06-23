package com.bad.bigad.controllers;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("/login")
    public Object login() {
        RMap<Object,Object> m = redissonClient.getMap("player:001");
        m.put("name", "ly");
        m.put("age", 18);
        return "Hello world";
    }

    @RequestMapping("/test")
    public Object test() {
        RMap<Object, Object> m = redissonClient.getMap("player:001");
        return null;
    }
}
