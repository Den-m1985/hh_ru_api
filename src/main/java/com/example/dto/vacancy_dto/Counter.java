package com.example.dto.vacancy_dto;

public record Counter(
        Integer responses,  // Количество откликов на вакансию с момента публикации
        Integer total_responses  // Количество откликов на вакансию с момента создания
) {
}
