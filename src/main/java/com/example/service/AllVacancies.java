package com.example.service;

import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.util.HeadHunterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AllVacancies {
    private final VacancyClient vacancyClient;
    private final HeadHunterProperties headHunterProperties;
    private final PaginationCalculator paginationCalculator;

    public Set<VacancyItem> getAllVacancies(String resumeId) {
        int total = headHunterProperties.getCountVacancies();
        int maxPerPage = 100;
        Map<Integer, Integer> pages = paginationCalculator.calculatePages(total, maxPerPage);
        Set<VacancyItem> all = new HashSet<>();

        for (Map.Entry<Integer, Integer> entry : pages.entrySet()) {
            int page = entry.getKey();
            int perPage = entry.getValue();
            ApiListResponse<VacancyItem> response = fetchVacancies(resumeId, page, perPage);
            all.addAll(response.items());
        }
        return all;
    }

    private ApiListResponse<VacancyItem> fetchVacancies(String resumeId, int page, int perPage) {
        if (Boolean.TRUE.equals(headHunterProperties.getSearchBySimilarVacancies())) {
            return vacancyClient.getSimilarVacancies(resumeId, page, perPage);
        } else {
            return vacancyClient.getSearchVacancies(page, perPage);
        }
    }

}
