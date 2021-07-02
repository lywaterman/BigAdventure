package com.bad.bigad.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RefreshScope
@Configuration
@ConfigurationProperties("servers")
@Data
public class ServerListConfig {
    private Map<Integer, String> map = new HashMap<>();
}
