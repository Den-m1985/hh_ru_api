package com.example.dto.it_map;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CompetencyMatrixFilterRequest(

        @NotBlank
        String specialization,

        @NotBlank
        @JsonProperty("experience_grade")
        String experienceGrade
) {
}
