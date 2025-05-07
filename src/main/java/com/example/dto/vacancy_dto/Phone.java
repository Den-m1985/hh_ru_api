package com.example.dto.vacancy_dto;

public record Phone(
        String city, // Код города
        String comment, // Комментарий (удобное время для звонка по этому номеру)
        String country,  // Код страны
        String formatted,  // Телефонный номер
        String number  // Телефон
) {
}
