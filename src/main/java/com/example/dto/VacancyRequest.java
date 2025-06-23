package com.example.dto;

import java.util.List;

public record VacancyRequest(
        String resumeId,
        int count,
        List<String> keywordsToExclude,
        boolean isSimilarSearch,
        String coverLetter,
        boolean enabledSchedule,

        // Основные фильтры
        String text,
        String search_field,
        String experience,
        String employment,
        String schedule,
        String area,
        String metro,
        String professional_role,
        String industry,
        String employer_id,
        String currency,
        Long salary,
        String label,

        // Флаги
        Boolean only_with_salary,
        Integer period,
        String date_from,
        String date_to,

        // Геокоординаты
        Double top_lat,
        Double bottom_lat,
        Double left_lng,
        Double right_lng,

        // Сортировка
        String order_by,
        Double sort_point_lat,
        Double sort_point_lng,

        // Дополнительные флаги
        Boolean clusters,
        Boolean describe_arguments,
        Boolean no_magic,
        Boolean premium,
        Boolean responses_count_enabled,
        String part_time,
        Boolean accept_temporary,

        // Интернационализация
        String locale,
        String host
) {
}
