package com.example.dto.agregator_dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompetencyMatrixRequest(

        String specialization,

        String competencies,

        @JsonProperty("technical_questions")
        String technicalQuestions
) {
}
