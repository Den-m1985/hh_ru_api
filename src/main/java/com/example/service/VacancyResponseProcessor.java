package com.example.service;

import com.example.dto.VacancyRequest;
import com.example.dto.vacancy_dto.VacancyItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyResponseProcessor {
    private final VacancyFilter vacancyFilter;
    private final AllVacancies allVacancies;
    private final CoverLetterService coverLetterService;
    private final VacancyResponseSender applicationService;

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Vakansii/operation/apply-to-vacancy">...</a>
     */
    public void respondToRelevantVacancies(VacancyRequest request) {
        List<VacancyItem> filtered = prepareData(request);
        for (VacancyItem vacancy : filtered) {
            try {
                String message = coverLetterService.prepareMessage(vacancy, request.coverLetter());
                applicationService.sendResponseToVacancy(request, vacancy, message);
            } catch (Exception e) {
                log.error("Failed to apply to vacancy: {}", vacancy.name(), e);
                Thread.currentThread().interrupt();
            }
        }
        log.info("Finished apply to vacancies with resume id: {}", request.resumeId());
    }

    public List<VacancyItem> prepareData(VacancyRequest request) {
        Set<VacancyItem> allVacanciesFromServer = allVacancies.getAllVacancies(request);
        List<VacancyItem> filteredData = vacancyFilter.filterVacancies(allVacanciesFromServer, request.keywordsToExclude());
        log.info("All vacancies size: {} Filtered vacancies size: {} according to resume id: {}",
                allVacanciesFromServer.size(),
                filteredData.size(),
                request.resumeId());
        return filteredData;
    }

}
