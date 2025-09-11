package com.example.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record CompanyResponseDto(
        Integer id,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        String category,

        String name,

        @JsonProperty("company_url")
        String companyUrl,

        @JsonProperty("career_url")
        String careerUrl,

        List<Integer> recruiters
) {
}
