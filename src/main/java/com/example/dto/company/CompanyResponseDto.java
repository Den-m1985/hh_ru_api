package com.example.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CompanyResponseDto(
        Integer id,

        String createdAt,

        String updatedAt,

        String category,

        String name,

        @JsonProperty("company_url")
        String companyUrl,

        @JsonProperty("career_url")
        String careerUrl,

        List<Integer> recruiters
) {
}
