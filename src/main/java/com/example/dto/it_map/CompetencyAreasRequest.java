package com.example.dto.it_map;

import java.util.List;

public record CompetencyAreasRequest(
        Integer id,
        String area,

        List<CompetencyRequest> competencies
) {
}
