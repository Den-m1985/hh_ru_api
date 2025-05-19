package com.example.service;

import com.example.dto.vacancy_dto.VacancyItem;
import com.example.util.ApplicationProperties;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyResponseSender {
    private final ApplicationProperties properties;
    private final RequestTemplates requestTemplates;
    private final DelayService delayService;

    public void sendResponseToVacancy(String resumeId, VacancyItem vacancy, String message) throws InterruptedException {
        if (properties.isDryRun()) {
            log.info("Dry run: would apply to {}", vacancy.name());
            return;
        }
        delayService.sleepRandom();
        requestTemplates.postDataToRequest(resumeId, vacancy.id(), message);
        log.info("Applied to vacancy: {}", vacancy.name());
    }

}
