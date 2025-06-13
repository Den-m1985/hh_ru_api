package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtAuthResponse {

    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;

}
