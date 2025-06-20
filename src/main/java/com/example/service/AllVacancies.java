package com.example.service;

import com.example.dto.VacancyRequest;
import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.VacancyItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AllVacancies {
    private final VacancyClient vacancyClient;
    private final PaginationCalculator paginationCalculator;
    private final int maxPerPage = 100;

    public Set<VacancyItem> getAllVacancies(VacancyRequest vacancyRequest) {
        Map<Integer, Integer> pages = paginationCalculator.calculatePages(vacancyRequest.count(), maxPerPage);
        Set<VacancyItem> all = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : pages.entrySet()) {
            int page = entry.getKey();
            int perPage = entry.getValue();
            ApiListResponse<VacancyItem> response = fetchVacancies(vacancyRequest, page, perPage);
            all.addAll(response.items());
        }
        return all;
    }

    private ApiListResponse<VacancyItem> fetchVacancies(VacancyRequest vacancyRequest, int page, int perPage) {
        if (vacancyRequest.isSimilarSearch()) {
            return vacancyClient.getSimilarVacancies(vacancyRequest, page, perPage);
        } else {
            return vacancyClient.getSearchVacancies(vacancyRequest, page, perPage);
        }
    }

}
