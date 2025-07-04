package com.example.service;

import com.example.dto.SavedSearchDto;
import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.model.User;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedSearches {
    private final RequestTemplates requestTemplates;
    String fetchUrl = "/saved_searches/vacancies";

    @Value("${hh.base_url_api}")
    private String baseUrlApi;

    // выводит список сохраненного поиска (сохраненный поиск в избранном)
    public void getSavedSearches(User user) {
        ApiListResponse<SavedSearchDto> data = requestTemplates.getSavedSearches(baseUrlApi + fetchUrl, user);
        String url = data.items().get(0).items().url();
        ApiListResponse<VacancyItem> responce = requestTemplates.getDataFromRequest(url, user.getHhToken());
    }
}
