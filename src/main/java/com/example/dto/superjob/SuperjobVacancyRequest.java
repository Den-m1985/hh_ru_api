package com.example.dto.superjob;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SuperjobVacancyRequest(

        @NotBlank
        String nameRequest,

        @NotNull
        Integer resumeId,

        Integer count,

        List<String> keywordsToExclude,

        String coverLetter,

        boolean enabledSchedule,

        SearchRequest searchRequest
) {
}
