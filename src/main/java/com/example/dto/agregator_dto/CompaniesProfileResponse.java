package com.example.dto.agregator_dto;

import com.example.dto.company.CompanyResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CompaniesProfileResponse(

        @JsonProperty("experience_grade_name")
        String ExperienceGradeName,

        @JsonProperty("experience_grade_description")
        String ExperienceGradeDescription,

        @JsonProperty("competency_specialization")
        String CompetencySpecialization,

        @JsonProperty("competencies")
        String competencies,

        @JsonProperty("competency_technical_questions")
        String CompetencyTechnicalQuestions,

        List<CompanyResponseDto> companiesResponse
) {
}
