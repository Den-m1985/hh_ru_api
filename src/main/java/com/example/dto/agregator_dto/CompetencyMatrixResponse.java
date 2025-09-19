package com.example.dto.agregator_dto;

public record CompetencyMatrixResponse(
        Integer id,

        String createdAt,

        String updatedAt,

        String specialization,

        String competencies,

        String technicalQuestions
) {
}
