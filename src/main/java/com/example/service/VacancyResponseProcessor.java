package com.example.service;

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
    private final ResumeService resumeService;
    private final VacancyFilter vacancyFilter;
    private final AllVacancies allVacancies;
    private final CoverLetterService coverLetterService;
    private final VacancyResponseSender applicationService;

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Vakansii/operation/apply-to-vacancy">...</a>
     */
    public void respondToRelevantVacancies() {
        String resumeId = resumeService.getResume().getResumeId();
        List<VacancyItem> filtered = prepareData(resumeId);

        for (VacancyItem vacancy : filtered) {
            try {
                String message = coverLetterService.prepareMessage(vacancy);
                applicationService.sendResponseToVacancy(resumeId, vacancy, message);
            } catch (Exception e) {
                log.error("Failed to apply to vacancy: {}", vacancy.name(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private List<VacancyItem> prepareData(String resumeId) {
        Set<VacancyItem> allVacanciesFromServer = allVacancies.getAllVacancies(resumeId);
        return vacancyFilter.filterVacancies(allVacanciesFromServer);
    }

}
