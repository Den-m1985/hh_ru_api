package com.example.dto.vacancy_dto;

public record WorkFormat(
        String id,  // Элементы из справочника work_format
        String name  // Название формата работы
) {
}

/*
"work_format": [
{
"id": "ON_SITE",
"name": "На месте работодателя"
},
{
"id": "REMOTE",
"name": "Из дома"
},
{
"id": "HYBRID",
"name": "Гибрид"
},
{
"id": "FIELD_WORK",
"name": "Разъездная"
}
],
 */
