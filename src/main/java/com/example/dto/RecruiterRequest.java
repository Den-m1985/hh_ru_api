package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecruiterRequest(
        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        @JsonProperty("contact_telegram")
        String contactTelegram,

        @JsonProperty("contact_linkedIn")
        String contactLinkedIn,

        String email,

        Integer company
) {
}
