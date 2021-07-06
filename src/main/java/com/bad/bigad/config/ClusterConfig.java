package com.bad.bigad.config;

import com.bad.bigad.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Map;

/*
    全局动态配置类, 会动态修改的信息在这里配置
 */
@Configuration
@Slf4j
@RefreshScope
public class ClusterConfig {
    @Value("${bad_sid}")
    String curServerID;

    @Value("${servers}")
    String serverList;

    Map<Integer, String> serverNodes;

    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh(RefreshScopeRefreshedEvent event) {
        serverNodes =  Util.gson.fromJson(serverList, Map.class);
    }

    public Map<Integer, String> getServerNodes() {
        if (serverNodes == null) {
            serverNodes =  Util.gson.fromJson(serverList, Map.class);
        }

        return serverNodes;
    }

    public String getNodeAddr(int sid) {
        return serverNodes.get(sid);
    }

    public String getChatQueueName() {
        return "topic_queue_" + curServerID;
    }
}
