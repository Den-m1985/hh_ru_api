package com.example.dto.agregator_dto;

import com.example.dto.it_map.CompetencyAreasResponse;

import java.util.List;

public record CompetencyMatrixResponse(
        Integer id,
        String specialization,
        String experienceGrade,
        List<CompetencyAreasResponse> competencyAreas
) {
}
