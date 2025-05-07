package com.example.dto;

import java.util.List;

public record SimilarVacanciesResponse(
        List<Vacancy> items,
        int found,
        int page,
        int pages,
        int per_page,
        String alternate_url
) {
}
