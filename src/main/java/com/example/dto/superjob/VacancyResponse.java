package com.example.dto.superjob;

import java.util.List;

public record VacancyResponse(
        List<Vacancy> objects,
        int total,
        boolean more,
        int subscription_id,
        boolean subscription_active
) {
}
