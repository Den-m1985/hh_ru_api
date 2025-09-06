package com.example.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CompanyResponseDto(

        String category,

        String name,

        @JsonProperty("company_url")
        String companyUrl,

        @JsonProperty("career_url")
        String careerUrl,

        List<Integer> recruiter
) {
}
