package com.example.dto.vacancy_dto;

public record SalaryRange(
        String currency,  // Код валюты из справочника currency
        Frequency frequency,// (Название частоты выплаты указанной зарплаты (object or null)) or (Null-объект (object or null))
        Integer from,  // Нижняя граница зарплаты
        Boolean gross,  // Признак что границы зарплаты указаны до вычета налогов
        Mode mode,  // Название типа грануляции указанной зарплаты
        Integer to  // Верхняя граница зарплаты
) {
}
