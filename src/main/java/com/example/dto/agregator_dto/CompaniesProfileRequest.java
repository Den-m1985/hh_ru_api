package com.example.dto.agregator_dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CompaniesProfileRequest(

        String specialization,

        @JsonProperty("experience_grade")
        Integer experienceGrade,

        List<String> categories
) {
}
