package com.example.dto.superjob;

import java.util.List;

public record SuperjobSearchRequest(

        Integer id_client,
        Integer id_user,
        Integer id_resume,
        Integer id_subs,

        // Фильтры по дате
        Long date_published_from,
        Long date_published_to,
        Long sort_new,

        // Статус публикации
        Integer published,
        Boolean published_all,
        Boolean archive,
        Boolean not_archive,

        // Поиск по ключевым словам
        String keyword,
        List<Keyword> keywords,

        // Сортировка
        String order_field,
        String order_direction,

        // Период и оклад
        Integer period,
        Integer payment_from,
        Integer payment_to,
        Integer no_agreement,

        // География
        Town town,
        List<Integer> m, // метро
        List<Integer> t, // города
        List<Integer> o, // области
        List<Integer> c, // страны

        // Отрасли и категории
        List<Integer> catalogues, // Integer, String или List<Integer>

        // Условия работы
        Integer place_of_work,
        Integer moveable,
        Integer agency,
        Integer type_of_work,

        // Кандидат
        Integer age,
        Integer gender,
        Integer education,
        Integer experience,

        // Водительские права
        List<String> driving_licence,
        Integer driving_particular,

        // Языки
        Integer language,
        Integer lang_level,
        Integer languages_particular,
        Integer nolang
) {
}
