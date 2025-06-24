package com.example.service;

import com.example.dto.HhTokenResponse;
import com.example.util.HttpUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class HhTokenRefreshClient {
    private final HttpUtils httpUtils;

    public HhTokenResponse refreshAccessToken(String url) {
        Map<String, String> headers = Map.of(
                "Content-Type", "application/x-www-form-urlencoded"
        );
        return httpUtils.safeRequest(
                url,
                HttpMethod.POST,
                headers,
                null,
                new TypeReference<>() {
                }
        );
    }

}
