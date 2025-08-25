package com.example.dto.superjob;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SuperjobTokenResponse(

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("ttl")
        Integer ttl,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("token_type")
        String tokenType
) {
}
