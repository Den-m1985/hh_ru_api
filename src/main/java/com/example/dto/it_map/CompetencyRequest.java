package com.example.dto.it_map;

import java.util.List;

public record CompetencyRequest(
        Integer id,
        String name,

        Integer experienceGradeId,
        String experienceGradeName,

        List<String> attribute
) {
}
