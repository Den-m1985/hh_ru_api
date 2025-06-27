package com.example.dto;

import com.example.dto.vacancy_dto.VacancyItem;

import java.util.List;

public record FilterResult(
        List<VacancyItem> filteredVacancies,
        List<VacancyItem> vacanciesWithTests
) {
}
