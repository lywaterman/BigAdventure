package com.bad.bigad.manager.game;

import com.bad.bigad.entity.Player;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LogicManager {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void processMsg(Player player, String msg) {
        executor.submit(() -> {
            if (player.getRoom() != null) {
                player.getRoom().onMessage(player, msg);
            }
        });
    }
}
