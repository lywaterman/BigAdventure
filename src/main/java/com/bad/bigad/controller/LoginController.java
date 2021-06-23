package com.bad.bigad.controller;

import com.bad.bigad.service.PlayerService;
import lombok.Data;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@Data
class LoginParam {
    private String wx_name;
    private String wx_nick_name;
}

@RestController
public class LoginController {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    PlayerService playerService;

    @RequestMapping("/login")
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public Object login(@RequestBody LoginParam param) {
//        RMap<Object,Object> m = redissonClient.getMap("player:001");
//        m.put("name", "ly");
//        m.put("age", 18);
        return param.getWx_name();
    }

    @RequestMapping("/test")
    public Object test() {
//        RMap<Object, Object> m = redissonClient.getMap("player:001");
//        return null;
        return playerService.findById(1).getWx_name();
    }
}
