package com.bad.bigad.controller;

import com.bad.bigad.config.ServerListConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RefreshScope
public class ConfigController {
//    @Value("#{${bad_slist}}")
//    Map<Integer, String> serverList;
final
ServerListConfig serverList;

    public ConfigController(ServerListConfig serverList) {
        this.serverList = serverList;
    }

    @RequestMapping("/config")
    public Map<Integer, String> getServerList() {
        return serverList.getMap();
    }
}
