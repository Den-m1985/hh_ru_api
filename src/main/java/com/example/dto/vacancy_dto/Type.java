package com.example.dto.vacancy_dto;

public record Type(
        String id,  // Тип из справочника vacancy_type
        String name  // Название типа вакансии
) {
}
