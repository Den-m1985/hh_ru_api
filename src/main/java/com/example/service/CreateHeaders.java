package com.example.service;

import com.example.model.HhToken;
import com.example.util.HeadHunterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateHeaders {
    private final TokenService tokenService;
    private final HeadHunterProperties headHunterProperties;

    public Map<String, String> getHeaders() {
        HhToken token = tokenService.findTokenByUserId(headHunterProperties.getClientId());
        return Map.of(
                "Authorization", "Bearer " + token.getAccessToken(),
                "HH-User-Agent", headHunterProperties.getHhUserAgent()
        );
    }

    public Map<String, String> createHeadersForToken() {
        return Map.of(
                "Content-Type", "application/x-www-form-urlencoded"
        );
    }
}
