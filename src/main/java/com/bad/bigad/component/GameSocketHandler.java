package com.bad.bigad.component;

import com.bad.bigad.config.ClusterConfig;
import com.bad.bigad.entity.Player;
import com.bad.bigad.manager.PlayerManager;
import com.bad.bigad.manager.ScriptManager;
import com.bad.bigad.manager.WsSessionManager;
import com.bad.bigad.model.PlayerOnlineStatus;
import com.bad.bigad.service.PlayerService;
import com.bad.bigad.util.BridgeForJs;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RefreshScope
public class GameSocketHandler extends TextWebSocketHandler {
    @Autowired
    PlayerService playerService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ClusterConfig clusterConfig;

    @Autowired
    ScriptManager scriptManager;

    @Value("${player_status_ttl}")
    public int player_status_ttl;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String strId = (String) session.getAttributes().get("id");
        Long id = Long.parseLong(strId);

        Player player = PlayerManager.instance.get(id);

        scriptManager.callJs("onMessage", message.getPayload(), session, player);
    }

    @Scheduled(fixedRate = 1000)
    public void testReload() {
        scriptManager.reload();
    }

    //可以处理同步登陆
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String strId = (String) session.getAttributes().get("id");
        Long id = Long.parseLong(strId);
        //检查是否在线

        Player player = playerService.GetPlayerFromCache(id);

        if (player == null) {
            session.sendMessage(new TextMessage("请先登陆"));
            session.close();
            return;
        } else {
            session.sendMessage(new TextMessage("登陆成功"));
        }

        RMapCache<Long, PlayerOnlineStatus> map = redissonClient.getMapCache("online_status");
        RLock lock = redissonClient.getLock(strId);

        if (lock.tryLock()) {
            try {

                PlayerOnlineStatus playerOnlineStatus = map.get(id);

                boolean online = (playerOnlineStatus != null) && (playerOnlineStatus.getServerId() != -1);

                if (online) {
                    //说明玩家在线
                    log.info("玩家在线，kick他"+player);

                    //优化
                    //先在本服务器找
                    playerService.kickPlayer(id, "您在其他地方登陆了");

                    //如果不再本服务器，rpc 调用
                    if (playerOnlineStatus.getServerId() != ClusterConfig.curServerID) {
                        String serverUrl = "http://" +
                                clusterConfig.getNodeAddr(playerOnlineStatus.getServerId()) +
                                "/kickPlayer?id={id}";

                        Boolean result = restTemplate.getForObject(
                                serverUrl,
                                Boolean.class,
                                id);

                        if (result == null || !result) {
                            session.sendMessage(new TextMessage("你正在游戏中，请稍后再试"));
                            session.close();
                            return;
                        }
                    }
                }

                //在本服务器上线
                WsSessionManager.instance.add(
                        id,
                        session);
                PlayerOnlineStatus status = new PlayerOnlineStatus(ClusterConfig.curServerID);
                PlayerManager.instance.add(
                        id,
                        player,
                        status);

                map.put(id, status, player_status_ttl, TimeUnit.SECONDS);

            } catch (Exception e) {
                log.error(e.getMessage());
                session.sendMessage(new TextMessage("服务器异常，请稍后再试"));
                session.close();
                return;
            } finally {
                lock.unlock();
            }
        } else {
            session.sendMessage(new TextMessage("同一账号同时登陆太多，请稍后再试"));
            session.close();
            return;
        }


        scriptManager.callJs("onLogin", session, player);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long id = Long.parseLong((String) session.getAttributes().get("id"));

        boolean removed = WsSessionManager.instance.remove(
                id,
                session
        );

        if (removed) {
            //给玩家下线
            PlayerManager.instance.remove(id);
        }
    }

}