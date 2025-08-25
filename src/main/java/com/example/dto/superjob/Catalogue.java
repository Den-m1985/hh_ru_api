package com.example.dto.superjob;

import java.util.List;

public record Catalogue(
        Integer id,
        String title,
        Integer key,
        List<Position> positions
) {
}
