package com.example.dto.agregator_dto;

import com.example.dto.it_map.CompetencyAreasRequest;

import java.util.List;

public record CompetencyMatrixRequest(
        Integer id,
        String specialization,

        List<CompetencyAreasRequest> competencies
) {
}
