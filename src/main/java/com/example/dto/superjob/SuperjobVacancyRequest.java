package com.example.dto.superjob;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SuperjobVacancyRequest(

        @NotBlank
        @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
        String nameRequest,

        @NotNull(message = "ID резюме обязательно")
        @Positive(message = "ID резюме должно быть положительным числом")
        Integer resumeId,

        Integer count,

        List<String> keywordsToExclude,

        String coverLetter,

        boolean enabledSchedule,

        SuperjobSearchRequest searchRequest
) {
}
