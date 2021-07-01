package com.bad.bigad.controller;

import com.bad.bigad.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @RequestMapping("/kickPlayer")
    public boolean kickPlayer(@RequestParam Long id) {
        return playerService.kickPlayer(id, "您在其他地方登陆了");
    }
}
