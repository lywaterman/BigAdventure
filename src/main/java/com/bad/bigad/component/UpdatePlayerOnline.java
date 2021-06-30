package com.bad.bigad.component;

import com.bad.bigad.entity.Player;
import com.bad.bigad.manager.PlayerManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UpdatePlayerOnline {
    @Value("${bad_sid}")
    int serverId;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    DiscoveryClient discoveryClient;

    @Scheduled(fixedRate = 5000)
    public void updatePlayerOnlineInfo() {
        RMapCache<Long, Object> map = redissonClient.getMapCache("online_status");
        map.putAll(PlayerManager.instance.getStatusMap(), 10, TimeUnit.SECONDS);
        log.info("test update");

        log.info(discoveryClient.getInstances("bad-starter").get(0).getUri().toString());
    }

}
