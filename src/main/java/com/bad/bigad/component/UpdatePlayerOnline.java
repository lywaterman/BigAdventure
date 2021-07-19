package com.bad.bigad.component;

import com.bad.bigad.config.ClusterConfig;
import com.bad.bigad.manager.PlayerManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RefreshScope
public class UpdatePlayerOnline implements ApplicationListener<RefreshScopeRefreshedEvent> {
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    PlayerManager playerManager;

    @Value("${player_status_ttl}")
    public int player_status_ttl;

    @Value("${update_player_status}")
    private String update_player_status;

    @Scheduled(fixedRateString = "${update_player_status}", initialDelay = 1000)
    public void updatePlayerOnlineInfo() {
        //RMapCache<Long, Object> map = redissonClient.getMapCache("online_status");
        RMap<Long, Object> map = redissonClient.getMap("online_status");
        map.clear();
        map.putAll(playerManager.getStatusMap());

        //更新完数据再设置超时
        map.expire(player_status_ttl, TimeUnit.SECONDS);
        //map.putAll(PlayerManager.instance.getStatusMap(), player_status_ttl, TimeUnit.SECONDS);
        log.info("更新在线玩家状态");
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        // TODO Auto-generated method stub

    }
}
