package com.example.dto.vacancy_dto;

public record Department(
        String id, // Департамент из справочника, от имени которого размещается вакансия (если данная возможность доступна для компании)
        String name  // Название департамента работодателя
) {
}
