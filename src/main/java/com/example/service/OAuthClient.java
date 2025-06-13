package com.example.service;

import com.example.dto.HhTokenResponse;
import com.example.model.AuthUser;
import com.example.model.HhToken;
import com.example.model.User;
import com.example.util.HeadHunterProperties;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthClient {
    private final RequestTemplates requestTemplates;
    private final HhTokenService tokenService;
    private final HeadHunterProperties headHunterProperties;

    /**
     * <a href="https://api.hh.ru/openapi/redoc#section/Avtorizaciya/Avtorizaciya-polzovatelya">...</a>
     */
    // TODO не работает добавление к запросу redirect_uri
    public String getAuthorizeUrl() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("response_type", "code");
        params.put("client_id", headHunterProperties.getClientId());
//        params.put("redirect_uri", headHunterProperties.getRedirectUri());
        return headHunterProperties.getBaseUrl() + "/oauth/authorize" + "?" + buildQuery(params);
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Avtorizaciya-soiskatelya/operation/authorize">...</a>
     */
    // согласно документации надо передавать в теле запроса, хотя в параметрах запроса тоже работает
    public void authenticate(String code, AuthUser authUser) {
        Map<String, String> params = Map.of(
                "client_id", headHunterProperties.getClientId(),
                "client_secret", headHunterProperties.getClientSecret(),
                "code", code,
                "grant_type", "authorization_code"
        );
        String url = headHunterProperties.getBaseUrl() + "/oauth/token" + "?" + buildQuery(params);
        getDataFromRequest(url, authUser);
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

    private void getDataFromRequest(String url, AuthUser authUser) {
        HhTokenResponse response = requestTemplates.getHhTokenFromRequest(url);

        User user = authUser.getUser();

        HhToken hhToken = new HhToken();
        hhToken.setAccessToken(response.getAccessToken());
        hhToken.setRefreshToken(response.getRefreshToken());
        hhToken.setTokenType(response.getTokenType());
        hhToken.setExpiresIn(response.getExpiresIn());
        log.info("Access Token: {}", hhToken.getAccessToken());
        user.setHhToken(hhToken);
        tokenService.saveToken(hhToken);
    }
}
