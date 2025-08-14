package com.example.dto.agregator_dto;

import com.example.dto.RecruiterDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AggregatorResponseDto(
        @JsonProperty("vacancy_name")
        String vacancyName,

        String url,

        @JsonProperty("company_name")
        String companyName,

        String grade,

        RecruiterDto recruiter
) {
}
