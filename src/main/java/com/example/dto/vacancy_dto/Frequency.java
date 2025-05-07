package com.example.dto.vacancy_dto;

public record Frequency(
        String id,  // Элементы из справочника salary_range_frequency
        String name  // Название частоты выплаты указанной зарплаты
) {
}
