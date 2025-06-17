package com.example.service;

import com.example.dto.HhTokenResponse;
import com.example.dto.StatePayload;
import com.example.enums.Device;
import com.example.model.AuthUser;
import com.example.model.HhToken;
import com.example.model.User;
import com.example.service.common.OAuthStateGenerator;
import com.example.service.common.UserService;
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
    private final OAuthStateGenerator oAuthStateGenerator;
    private final UserService userService;

    /**
     * <a href="https://api.hh.ru/openapi/redoc#section/Avtorizaciya/Avtorizaciya-polzovatelya">...</a>
     */
    public String getAuthorizeUrl(AuthUser authUser) {
        String state = oAuthStateGenerator.generateEncryptedState(authUser.getUser().getId(), Device.ANDROID);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("response_type", "code");
        params.put("client_id", headHunterProperties.getClientId());
        params.put("state", state);
        String url = headHunterProperties.getBaseUrl() + "/oauth/authorize" + "?" + buildQuery(params);
        System.out.println(url);
        return url;
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Avtorizaciya-soiskatelya/operation/authorize">...</a>
     */
    // согласно документации надо передавать в теле запроса, хотя в параметрах запроса тоже работает
    public void authenticate(String code, StatePayload statePayload) {
        Map<String, String> params = Map.of(
                "client_id", headHunterProperties.getClientId(),
                "client_secret", headHunterProperties.getClientSecret(),
                "code", code,
                "grant_type", "authorization_code"
        );
        String url = headHunterProperties.getBaseUrl() + "/oauth/token" + "?" + buildQuery(params);
        getTokenFromService(url, statePayload);
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

    private void getTokenFromService(String url, StatePayload statePayload) {
        HhTokenResponse response = requestTemplates.getHhTokenFromRequest(url);
        User user = userService.getUserById(statePayload.userId());

        HhToken hhToken = user.getHhToken();
        if (hhToken == null) {
            hhToken = new HhToken();
            hhToken.setUser(user);
        }
        hhToken.setAccessToken(response.getAccessToken());
        hhToken.setRefreshToken(response.getRefreshToken());
        hhToken.setTokenType(response.getTokenType());
        hhToken.setExpiresIn(response.getExpiresIn());
        log.info("Access Token: {}", hhToken.getAccessToken());
        user.setHhToken(hhToken);
        tokenService.saveToken(hhToken);
    }
}
