package com.example.dto.vacancy_dto;

public record ProfessionalRoles(
        /*
        Идентификатор профессиональной роли. Элемент справочника professional_roles
        https://api.hh.ru/openapi/redoc#tag/Obshie-spravochniki/operation/get-professional-roles-dictionary
         */
        String id,
        String name  // Название профессиональной роли
) {
}
