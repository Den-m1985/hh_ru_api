package com.example.dto.superjob;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record SendCvOnVacancyDto(

        @NotNull
        @JsonProperty("id_cv")
        Integer idCv,

        @NotNull
        @JsonProperty("id_vacancy")
        Integer idVacancy,

        String comment
) {
}
