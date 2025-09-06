package com.example.dto.agregator_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyCategoryDto(
        Integer id,

        @Schema(
                description = """
                Название категории компании.
                Возможные значения:
                 - BigTech
                 - Food-Tech
                 - Telecom
                 - Fin-Tech
                 - Travel-Tech
                 - Аутсорс / Аутстафф / Интеграторы
                 - E-com
                 - Ed-Tech
                 - Кибербез
                 - Медиа
                 - Госкомпании
                """,
                example = "Fin-Tech"
        )
        @NotBlank
        @Size(min = 2, max = 100, message = "Название должно быть от 3 до 100 символов")
        String name,

        @Schema(description = "Описание категории компании", example = "Финансовые технологии и сервисы")
        @NotBlank
        @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
        String description
) {
}
