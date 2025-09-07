package com.example.service.superjob;

import com.example.dto.superjob.SuperjobTokenResponse;
import com.example.model.SuperjobToken;
import com.example.model.User;
import com.example.repository.SuperjobTokenRepository;
import com.example.util.HttpUtils;
import com.example.util.QueryBuilder;
import com.example.util.SuperjobProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperjobTokenService {
    private final SuperjobTokenRepository superjobTokenRepository;
    private final SuperjobProperties superjobProperties;
    private final HttpUtils httpUtils;

    public SuperjobToken saveToken(SuperjobToken newToken) {
        return superjobTokenRepository.save(newToken);
    }

    public SuperjobToken getTokenFromDb(User user) {
        return superjobTokenRepository.findSuperjobTokenByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("SuperjobToken with user_id: " + user.getId() + " not found"));
    }

    public boolean isTokenGood(SuperjobToken superjobToken) {
        if (superjobToken == null
                || superjobToken.getAccessToken() == null
                || superjobToken.getUpdatedAt() == null
                || superjobToken.getExpiresIn() == null) {
            return false;
        }
        LocalDateTime expirationTime = superjobToken.getUpdatedAt().plusSeconds(superjobToken.getExpiresIn());
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expirationTime);
    }

    @Transactional
    public SuperjobToken refreshTokens(SuperjobToken superjobToken) {
        if (superjobToken == null) {
            return null;
        }
        Map<String, String> params = Map.of(
                "refresh_token", superjobToken.getRefreshToken(),
                "client_id", superjobProperties.clientId(),
                "client_secret", superjobProperties.clientSecret()
        );
        String url = superjobProperties.baseUrlApi() + "/2.0/oauth2/refresh_token/" + "?" + QueryBuilder.buildQuery(params);

        Map<String, String> headers = Map.of(
                "Content-Type", "application/x-www-form-urlencoded"
        );
        SuperjobTokenResponse response = httpUtils.safeRequest(
                url,
                HttpMethod.POST,
                headers,
                null,
                new TypeReference<>() {
                }
        );
        superjobToken.setAccessToken(response.accessToken());
        superjobToken.setRefreshToken(response.refreshToken());
        superjobToken.setTtl(response.ttl());
        superjobToken.setExpiresIn(response.expiresIn());
        superjobToken.setTokenType(response.tokenType());
        log.info("Access Token: {} for user:{}", superjobToken.getAccessToken(), superjobToken.getUser().getId());
        return saveToken(superjobToken);
    }

}
