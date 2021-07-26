package com.bad.bigad.manager.game;

import com.bad.bigad.entity.Player;
import com.bad.bigad.manager.ScriptManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LogicManager {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    ScriptManager scriptManager;
//
//    public void processFunc(String name, Object... args) {
//        executor.submit(() -> {
//            scriptManager.callJs(name, args);
//        });
//    }

    //同步处理消息
    public void processMsg(Player player, String msg) {
        executor.submit(() -> {
            if (player.getRoom() != null) {
                player.getRoom().onMessage(player, msg);
            }
        });
    }
}
