package com.example.dto.it_map;

import java.util.List;

public record CompetencyResponse(
        Integer id,
        String name,
        List<String> attributes
) {
}
