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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
@Component
public class GameSocketHandler extends TextWebSocketHandler {

    @Value("${bad_sid}")
    int serverId;

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

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
//        for(WebSocketSession webSocketSession : sessions) {
//            Map value = new Gson().fromJson(message.getPayload(), Map.class);
//            webSocketSession.sendMessage(new TextMessage("Hello " + value.get("name") + " !"));
//        }
//        String userName = (String) session.getAttributes().get("id");
//        session.sendMessage(new TextMessage("Hello " + userName));

        scriptManager.callJs("onMessage", message.getPayload(), session, BridgeForJs.instance);
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
                    if (playerOnlineStatus.getServerId() != serverId) {
                        restTemplate.getForObject(
                                new StringBuilder()
                                        .append("http://")
                                        .append(clusterConfig.getNodeAddr(playerOnlineStatus.getServerId()))
                                        .append("/kickPlayer?id={id}").toString(),
                                Boolean.class,
                                id);
                    }
                }

                //在本服务器上线
                WsSessionManager.instance.add(
                        id,
                        session);
                PlayerOnlineStatus status = new PlayerOnlineStatus(serverId);
                PlayerManager.instance.add(
                        id,
                        player,
                        status);

            } catch (Exception e) {
                log.error(String.valueOf(e.getStackTrace()));
            } finally {
                lock.unlock();
            }
        } else {
            session.sendMessage(new TextMessage("同一账号同时登陆太多，请稍后再试"));
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long id = Long.parseLong((String) session.getAttributes().get("id"));

        WsSessionManager.instance.remove(
                id
        );
        PlayerManager.instance.remove(
                id
        );
    }

}