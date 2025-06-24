package com.example.service;

import com.example.dto.HhTokenResponse;
import com.example.model.AuthUser;
import com.example.model.HhToken;
import com.example.repository.HhTokenRepository;
import com.example.util.HeadHunterProperties;
import com.example.util.QueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HhTokenService {
    private final HhTokenRepository tokenRepository;
    private final HhTokenRefreshClient hhTokenRefreshClient;
    private final HeadHunterProperties headHunterProperties;

    public void saveToken(HhToken newToken) {
        tokenRepository.save(newToken);
    }

    public boolean checkToken(AuthUser authUser) {
        return isTokenGood(authUser.getUser().getHhToken());
    }

    public boolean isTokenGood(HhToken hhToken) {
        if (hhToken == null
                || hhToken.getAccessToken() == null
                || hhToken.getUpdatedAt() == null
                || hhToken.getExpiresIn() == null) {
            return false;
        }
        LocalDateTime expirationTime = hhToken.getUpdatedAt().plusSeconds(hhToken.getExpiresIn());
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expirationTime);
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#section/Avtorizaciya/Obnovlenie-pary-access-i-refresh-tokenov">...</a>
     */
    @Transactional
    public boolean refreshHhTokens(HhToken hhToken) {
        if (hhToken == null) {
            return false;
        }
        Map<String, String> params = Map.of(
                "grant_type", "refresh_token",
                "refresh_token", hhToken.getRefreshToken()
        );
        String url = headHunterProperties.getBaseUrlApi() + "/token" + "?" + QueryBuilder.buildQuery(params);
        HhTokenResponse response = hhTokenRefreshClient.refreshAccessToken(url);
        if (response == null) {
            return false;
        }
        hhToken.setAccessToken(response.getAccessToken());
        hhToken.setRefreshToken(response.getRefreshToken());
        hhToken.setTokenType(response.getTokenType());
        hhToken.setExpiresIn(response.getExpiresIn());
        log.info("New access Token: {} for user id:{}", hhToken.getAccessToken(), hhToken.getUser().getId());
        saveToken(hhToken);
        return true;
    }
}
