package com.example.dto.vacancy_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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
                description = "Широта региона",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Double lat,

        @NotBlank
        @Schema(
                description = "Долгота региона",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Double lng,

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
        List<Area> areas,

        @Schema(
                description = "Часовой пояс региона в формате UTC (например, '+03:00')",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                example = "+03:00"
        )
        @JsonProperty("utc_offset")
        String utcOffset
) {
}
