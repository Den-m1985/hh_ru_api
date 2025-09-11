package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record RecruiterDto(
        Integer id,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        @JsonProperty("contact_telegram")
        String contactTelegram,

        @JsonProperty("contact_linkedIn")
        String contactLinkedIn,

        String email,

        String company
) {
}
