package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecruiterDto(

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        String contact,

        String company
) {
}
