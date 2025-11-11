package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VacancyHistoryDto(

        @JsonProperty("history_id")
        Integer historyId,

        String provider,

        @JsonProperty("vacancy_url")
        String vacancyUrl,

        @JsonProperty("user_id")
        Integer userId
) {
}
