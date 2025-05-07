package com.example.dto.vacancy_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResumeDto(
        Integer found,  // Найдено результатов
        Integer page,  // Номер страницы
        Integer pages,  // Всего страниц
        Integer per_page,  // Результатов на странице
        List<ResumeItemDto> items
) {
}
