package com.example.dto.vacancy_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Employer(
        Boolean accredited_it_employer,  // Флаг, показывающий, прошла ли компания IT аккредитацию
        String alternate_url, // Ссылка на представление компании на сайте
        EmployerRating employer_rating,  // Информация о рейтинге работодателя
        String id,
        Object logo_urls,
        String name,  // Название компании
        Boolean trusted,  // Флаг, показывающий, прошла ли компания проверку на сайте
        String url,  // URL, на который нужно сделать GET-запрос, чтобы получить информацию о компании
        String vacancies_url,  // Ссылка на поисковую выдачу вакансий данной компании
        Integer country_id
) {
}
