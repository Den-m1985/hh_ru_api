package com.example.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    Rsa rsa,
    Lifetime lifetime
) {
    public record Rsa(
        String privateKey,
        String publicKey
    ) {}

    public record Lifetime(
        Integer access,
        Integer refresh
    ) {}
}