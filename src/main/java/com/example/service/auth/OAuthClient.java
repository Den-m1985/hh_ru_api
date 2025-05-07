package com.example.service.auth;

import com.example.dto.HhTokenResponse;
import com.example.model.HhToken;
import com.example.service.CreateHeaders;
import com.example.util.HttpUtils;
import com.example.service.TokenService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class OAuthClient {
    private final HttpUtils httpUtils;
    private final TokenService tokenService;
    private final CreateHeaders createHeaders;

    @Value("${hh.client-id}")
    private String clientId;

    @Value("${hh.client-secret}")
    private String clientSecret;

    @Value("${hh.base_url}")
    private String baseUrl;

    public String getAuthorizeUrl() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        return baseUrl + "/oauth/authorize" + "?" + buildQuery(params);
    }

    public HhToken authenticate(String code) {
        Map<String, String> params = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code,
                "grant_type", "authorization_code"
        );
        String url = baseUrl + "/oauth/token" + "?" + buildQuery(params);
        return getDataFromRequest(url);
    }

    private String buildQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (var entry : params.entrySet()) {
            if (!sb.isEmpty()) sb.append("&");
            sb.append(URLEncoder.encode(entry.getKey(), UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), UTF_8));
        }
        return sb.toString();
    }

    private HhToken getDataFromRequest(String url) {
        HhTokenResponse response = httpUtils.safeRequest(
                url,
                HttpMethod.POST,
                createHeaders.createHeadersForToken(),
                null,
                new TypeReference<>() {
                }
        );
        HhToken hhToken = new HhToken();
        hhToken.setAccessToken(response.getAccessToken());
        hhToken.setRefreshToken(response.getRefreshToken());
        hhToken.setToken_type(response.getTokenType());
        hhToken.setExpiresIn(response.getExpiresIn());
        hhToken.setUserId(clientId);
        return tokenService.saveToken(hhToken);
    }
}
