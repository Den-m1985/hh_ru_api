package com.example.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Integer userId
) {
}
