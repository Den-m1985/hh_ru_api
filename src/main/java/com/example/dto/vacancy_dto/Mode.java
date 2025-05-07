package com.example.dto.vacancy_dto;

public record Mode(
        String id,  // Элементы из справочника salary_range_mode
        String name  // Название типа грануляции указанной зарплаты
) {
}
