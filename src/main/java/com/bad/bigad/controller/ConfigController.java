package com.bad.bigad.controller;

import com.bad.bigad.config.ClusterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RefreshScope
public class ConfigController {
    @Autowired
    ClusterConfig clusterConfig;

    @RequestMapping("/config")
    public Map getServerList() {
        return clusterConfig.getServerNodes();
    }
}
