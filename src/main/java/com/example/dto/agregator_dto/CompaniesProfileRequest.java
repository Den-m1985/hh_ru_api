package com.example.dto.agregator_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CompaniesProfileRequest(

        @Schema(
                description = """
                        Возможные значения:
                         - Java
                         - Python
                         - Go
                         - C++
                         - Frontend
                         - QA
                         - iOS
                         - Android
                         - DA
                         - Data Science
                         - DevOps
                         - Project Manager
                         - Product Manager
                         - ...
                        """,
                example = "Java"
        )
        @NotBlank
        @Size(min = 1, max = 100, message = "Название должно быть от 2 до 100 символов")
        String specialization,


        @Schema(
                description = """
                        Возможные значения:
                         - Junior
                         - Middle
                         - ...
                        """,
                example = "Middle"
        )
        @JsonProperty("experience_grade")
        Integer experienceGrade,


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
                         - ...
                        """,
                example = "Fin-Tech"
        )
        List<String> categories
) {
}
