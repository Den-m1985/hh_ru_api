package com.example.dto.vacancy_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record Area(
        @NotBlank
        @Schema(
                description = "ID региона",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "1"
        )
        String id,

        @NotBlank
        @Schema(
                description = "Название региона",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "Москва"
        )
        String name,

        @Schema(
                description = "Название в предложном падеже (например, 'в Москве')",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "Москве"
        )
        String name_prepositional,

        @Schema(
                description = "ID родительского региона",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "113"
        )
        String parent_id,

        @NotBlank
        @Schema(description = "Дочерние регионы", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<Area> areas
) {
}
