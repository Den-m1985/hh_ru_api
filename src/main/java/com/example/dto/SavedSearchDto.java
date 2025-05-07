package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SavedSearchDto(
        String created_at,  // Дата и время создания
        String id,  // Идентификатор поиска
        FavoriteSearchDto items,  // Данные о списке найденных вакансий/резюме
        String name,  // Название поиска
        Object new_items,
        // Данные о найденных вакансиях/резюме, появившихся с момента последнего просмотра автопоиска. При переходе по ссылке счетчик новых элементов будет сброшен (возможна небольшая задержка по времени). Счетчик new_items.count обновляется раз в час. Из-за этого реальное количество новых найденных элементов может расходиться со значением этого счетчика
        Boolean subscription  // Статус подписки
) {
}
