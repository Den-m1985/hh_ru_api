package com.example.service;

import com.example.dto.HhTokenResponse;
import com.example.dto.StatePayload;
import com.example.dto.superjob.SuperjobTokenResponse;
import com.example.enums.Device;
import com.example.model.AuthUser;
import com.example.model.HhToken;
import com.example.model.SuperjobToken;
import com.example.model.User;
import com.example.service.common.OAuthStateGenerator;
import com.example.service.common.UserService;
import com.example.service.superjob.SuperjobTokenService;
import com.example.util.HeadHunterProperties;
import com.example.util.QueryBuilder;
import com.example.util.RequestTemplates;
import com.example.util.SuperjobProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthClient {
    private final RequestTemplates requestTemplates;
    private final HhTokenService tokenService;
    private final SuperjobTokenService superjobTokenService;
    private final HeadHunterProperties headHunterProperties;
    private final OAuthStateGenerator oAuthStateGenerator;
    private final UserService userService;
    private final SuperjobProperties superjobProperties;

    /**
     * <a href="https://api.hh.ru/openapi/redoc#section/Avtorizaciya/Avtorizaciya-polzovatelya">...</a>
     */
    public String getAuthorizeUrl(AuthUser authUser) {
        String state = oAuthStateGenerator.generateEncryptedState(authUser.getUser().getId(), Device.ANDROID);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("response_type", "code");
        params.put("client_id", headHunterProperties.getClientId());
        params.put("state", state);
        String url = headHunterProperties.getBaseUrl() + "/oauth/authorize" + "?" + QueryBuilder.buildQuery(params);
        System.out.println(url);
        return url;
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Avtorizaciya-soiskatelya/operation/authorize">...</a>
     */
    // согласно документации надо передавать в теле запроса, хотя в параметрах запроса тоже работает
    public void authenticateHeadHunter(String code, StatePayload statePayload) {
        Map<String, String> params = Map.of(
                "client_id", headHunterProperties.getClientId(),
                "client_secret", headHunterProperties.getClientSecret(),
                "code", code,
                "grant_type", "authorization_code"
        );
        String url = headHunterProperties.getBaseUrl() + "/oauth/token" + "?" + QueryBuilder.buildQuery(params);
        getTokenFromService(url, statePayload);
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
        log.info("Access Token: {} for user:{}", hhToken.getAccessToken(), user.getId());
        user.setHhToken(hhToken);
        tokenService.saveToken(hhToken);
    }

    public void authenticateSuperJobUser(String code, StatePayload statePayload) {
        Map<String, String> params = Map.of(
                "code", code,
                "redirect_uri",superjobProperties.redirectUri(),
                "client_id", superjobProperties.clientId(),
                "client_secret", superjobProperties.clientSecret()
        );
        String url = superjobProperties.baseUrlApi() + "/2.0/oauth2/access_token/" + "?" + QueryBuilder.buildQuery(params);
        getTokenFromSuperjob(url, statePayload);
    }

    private void getTokenFromSuperjob(String url, StatePayload statePayload) {
        SuperjobTokenResponse response = requestTemplates.getSuperjobTokenFromRequest(url);
        User user = userService.getUserById(statePayload.userId());

        SuperjobToken superjobToken = user.getSuperjobToken();
        if (superjobToken == null) {
            superjobToken = new SuperjobToken();
            superjobToken.setUser(user);
        }
        superjobToken.setAccessToken(response.accessToken());
        superjobToken.setRefreshToken(response.refreshToken());
        superjobToken.setTtl(response.ttl());
        superjobToken.setExpiresIn(response.expiresIn());
        superjobToken.setTokenType(response.tokenType());
        log.info("Access Token: {} for user:{}", superjobToken.getAccessToken(), user.getId());
        user.setSuperjobToken(superjobToken);
        superjobTokenService.saveToken(superjobToken);
    }
}
