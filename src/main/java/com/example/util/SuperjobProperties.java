package com.example.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "superjob")
public record SuperjobProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String baseUrl,
        String baseUrlApi,
        String userAgent
) {
}
