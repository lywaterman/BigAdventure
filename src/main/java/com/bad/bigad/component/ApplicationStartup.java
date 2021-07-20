package com.bad.bigad.component;

import com.bad.bigad.service.game.GameRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationStartup implements ApplicationListener <ApplicationReadyEvent> {
    @Autowired
    private GameRoomService gameRoomService;
    @Override public void onApplicationEvent(final ApplicationReadyEvent event) {
        log.info("游戏房间服务启动。。。");
        gameRoomService.init();
        log.info("游戏房间服务启动完成");
    }
}
