package com.example.service;

import com.example.model.HhToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateHeaders {
    private final TokenService tokenService;

    @Value("${hh.client-id}")
    private String clientId;

    @Value("${hh.HH-User-Agent}")
    private String HhUserAgent;

    public Map<String, String> getHeaders() {
        HhToken token = tokenService.findTokenByUserId(clientId);
        return Map.of(
                "Authorization", "Bearer " + token.getAccessToken(),
                "HH-User-Agent", HhUserAgent
        );
    }

    public Map<String, String> createHeadersForToken() {
        return Map.of(
                "Content-Type", "application/x-www-form-urlencoded"
        );
    }
}
