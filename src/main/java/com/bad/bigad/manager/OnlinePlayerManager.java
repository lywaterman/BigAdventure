package com.bad.bigad.manager;

import com.bad.bigad.entity.Player;
import com.bad.bigad.model.PlayerOnlineStatus;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@RefreshScope
@Component
public class OnlinePlayerManager implements ApplicationListener<RefreshScopeRefreshedEvent>  {
    @Value("${player_status_ttl}")
    public int player_status_ttl;

    @Value("${update_player_status}")
    private String update_player_status;

    @Autowired
    RedissonClient redissonClient;

    private ConcurrentHashMap<Long, Player> players;
    {
        players = new ConcurrentHashMap<>();
    }

    private ConcurrentHashMap<Long, PlayerOnlineStatus> status_map;
    {
        status_map = new ConcurrentHashMap<>();
    }

    @Scheduled(fixedRateString = "${update_player_status}", initialDelay = 1000)
    public void updatePlayerOnlineInfo() {
        //RMapCache<Long, Object> map = redissonClient.getMapCache("online_status");
        RMapCache<Long, Object> map = redissonClient.getMapCache("online_status");

        map.putAll(getStatusMap(), player_status_ttl, TimeUnit.SECONDS);

        //更新完数据再设置超时
        map.expire(player_status_ttl, TimeUnit.SECONDS);
        //map.putAll(PlayerManager.instance.getStatusMap(), player_status_ttl, TimeUnit.SECONDS);
        //log.info("更新在线玩家状态");
    }

    //Session Link Player
//    private ConcurrentHashMap<Player, WebSocketSession> playerSessionLink;
//    {
//        playerSessionLink = new ConcurrentHashMap<>();
//    }
//
//    public void link(WebSocketSession session, Player player) {
//        playerSessionLink.put(player, session);
//    }
//
//    public void unlink(WebSocketSession session, Player player) {
//        playerSessionLink.remove(player, session);
//    }


    public ConcurrentHashMap<Long, PlayerOnlineStatus> getStatusMap() {
        return status_map;
    }

    public void add(Long playerId, Player player, PlayerOnlineStatus status) {
        players.put(playerId, player);
        status_map.put(playerId, status);
    }

    public Player remove(Long playerId) {
        status_map.remove(playerId);
        return players.remove(playerId);
    }

    public Player get(Long playerId) {
        return players.get(playerId);
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent refreshScopeRefreshedEvent) {

    }
}
