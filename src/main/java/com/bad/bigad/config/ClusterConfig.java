package com.bad.bigad.config;

import com.bad.bigad.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
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
    @Value("${servers}")
    String serverList;

    static public int curServerID;

    static {
        curServerID = Integer.parseInt(System.getenv("bad_sid"));
    }

    Map<Integer, String> serverNodes;

    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh(RefreshScopeRefreshedEvent event) {
        ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<Map<Integer, String>>() {
        });
        try {
            serverNodes = reader.readValue(serverList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, String> getServerNodes() {
        if (serverNodes == null) {
            ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<Map<Integer, String>>() {
            });
            try {
                serverNodes = reader.readValue(serverList);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return serverNodes;
    }

    public String getNodeAddr(int sid) {
        getServerNodes();
        return serverNodes.get(sid);
    }

    public String getChatQueueName() {
        return "topic_queue_" + curServerID;
    }
}
