package com.example.dto.it_map;

import java.util.List;

public record CompetencyAreasResponse(
        Integer id,
        String area,
        List<CompetencyResponse> competencies
) {
}
