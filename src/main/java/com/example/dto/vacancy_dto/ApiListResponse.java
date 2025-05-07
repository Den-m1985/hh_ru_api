package com.example.dto.vacancy_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiListResponse<T>(
        List<T> items,
        int found,  // Найдено результатов
        int page,  // Номер страницы
        int pages,  // Всего страниц
        int per_page,  // Результатов на странице
        String alternate_url  // Ссылка на вакансию
) {
}
