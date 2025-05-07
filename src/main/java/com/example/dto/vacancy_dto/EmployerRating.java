package com.example.dto.vacancy_dto;

public record EmployerRating(
        Integer reviews_count,   // Количество отзывов
        String total_rating  // Сводный рейтинг компании
) {
}
