package com.example.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    private String prePrompt;
    private float applyIntervalMin;
    private float applyIntervalMax;
    private float pageIntervalMin;
    private float pageIntervalMax;
    private boolean dryRun;
}
