package com.example.dto.vacancy_dto;

import com.example.enums.VacancyRelation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VacancyItem(
        Boolean accept_incomplete_resumes,  // Разрешен ли отклик на вакансию неполным резюме
        Boolean accept_temporary, // Указание, что вакансия доступна с временным трудоустройством
        String alternate_url, // Ссылка на представление вакансии на сайте
        String apply_alternate_url,  // Ссылка на отклик на вакансию на сайте
        Boolean archived,  //  Находится ли данная вакансия в архиве
        AreaFromVacancy area,  //  Регион
        Contact contacts,  // Контактная информация
        String created_at,  // Дата и время публикации вакансии
        Department department,  // Департамент
        Employer employer,  // Информация о компании работодателя
        Boolean has_test,  // Информация о наличии прикрепленного тестового задании к вакансии
        String id,
        Boolean internship,  // Стажировка
        String name,
        Boolean premium,  // Является ли данная вакансия премиум-вакансией
        List<ProfessionalRoles> professional_roles,  // Список профессиональных ролей
        String published_at,  // Дата и время публикации вакансии
        List<VacancyRelation> relations,  // Связи соискателя с вакансией Enum
        Boolean response_letter_required,
        String response_url,  // URL отклика для прямых вакансий (type.id=direct)
        SalaryRange salary_range,  // Зарплата
        Boolean show_contacts,  // Доступны ли контакты в вакансии
        Type type,
        String url,  // URL вакансии
        List<WorkFormat> work_format,  // Список форматов работы
        Counter counters,  // Число откликов на вакансию
        Experience experience,  // Опыт работы
        Snippet snippet  // Дополнительные текстовые отрывки
) {
}
