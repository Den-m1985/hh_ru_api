package com.example.dto.superjob.resume;

import com.example.dto.superjob.Town;

public record EducationHistory(
        String institute,
        Town town,
        String townName,
        String name,
        Integer yearend,
        String certificate_url
) {
}
