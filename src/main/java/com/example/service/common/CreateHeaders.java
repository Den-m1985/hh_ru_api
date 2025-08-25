package com.example.service.common;

import com.example.model.HhToken;
import com.example.model.SuperjobToken;
import com.example.service.HhTokenService;
import com.example.service.superjob.SuperjobTokenService;
import com.example.util.HeadHunterProperties;
import com.example.util.SuperjobProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateHeaders {
    private final HeadHunterProperties headHunterProperties;
    private final SuperjobProperties superjobProperties;
    private final HhTokenService hhTokenService;
    private final SuperjobTokenService superjobTokenService;

    public Map<String, String> getHeaders(HhToken token) {
        if (token == null) {
            throw new RuntimeException("Token for request null");
        }
        if (!hhTokenService.isTokenGood(token)) {
            boolean refreshed = hhTokenService.refreshHhTokens(token);
            if (!refreshed) {
                throw new RuntimeException("Token from hh.ru no good and refresh failed for user id: " + token.getUser().getId());
            }
        }
        return Map.of(
                "Authorization", "Bearer " + token.getAccessToken(),
                "HH-User-Agent", headHunterProperties.getHhUserAgent()
        );
    }

    public Map<String, String> createHeadersSuperjob(SuperjobToken token) {
        if (token == null) {
            throw new RuntimeException("Token for request null");
        }
        if (!superjobTokenService.isTokenGood(token)) {
            throw new RuntimeException("Нужно обновить токен");
        }
        return Map.of(
                "Authorization", "Bearer " + token.getAccessToken(),
                "X-Api-App-Id", superjobProperties.clientSecret(),
                "Content-Type", "application/x-www-form-urlencoded"

        );
    }

    public Map<String, String> createHeadersForToken() {
        return Map.of(
                "Content-Type", "application/x-www-form-urlencoded"
        );
    }
}
