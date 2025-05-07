package com.example.dto.vacancy_dto;

public record Snippet(
        String requirement,  // Отрывок из требований по вакансии, если они найдены в тексте описания
        String responsibility  // Отрывок из обязанностей по вакансии, если они найдены в тексте описания
) {
}
