package com.bad.bigad.component;

import com.bad.bigad.manager.PlayerManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UpdatePlayerOnline {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    DiscoveryClient discoveryClient;

    @Scheduled(fixedRate = 10000)
    public void updatePlayerOnlineInfo() {
        RMapCache<Long, Object> map = redissonClient.getMapCache("online_status");
        map.putAll(PlayerManager.instance.getStatusMap(), 30, TimeUnit.SECONDS);
        log.info("更新在线玩家状态");
    }

}
