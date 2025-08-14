package com.example.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "habr")
public record HabrProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String habrUserAgent,
        String baseUrlApi,
        String baseUrl
) {
}
