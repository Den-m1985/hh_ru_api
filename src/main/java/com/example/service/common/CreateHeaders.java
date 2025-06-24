package com.example.service.common;

import com.example.model.HhToken;
import com.example.service.HhTokenService;
import com.example.util.HeadHunterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateHeaders {
    private final HeadHunterProperties headHunterProperties;
    private final HhTokenService hhTokenService;

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

    public Map<String, String> createHeadersForToken() {
        return Map.of(
                "Content-Type", "application/x-www-form-urlencoded"
        );
    }
}
