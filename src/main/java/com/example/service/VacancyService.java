package com.example.service;

import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.util.ApplicationProperties;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyClient vacancyClient;
    private final GenerateChatClient blackboxChatClient;
    private final ApplicationProperties properties;
    private final ResumeService resumeService;
    private final RequestTemplates requestTemplates;
    private final NegotiationsAll negotiationsAll;

    @Value("${hh.cover_letter}")
    private String cover_letter;

    @Value("${hh.count_vacancies}")
    private int countVacancies;

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Vakansii/operation/apply-to-vacancy">...</a>
     */
    public void applyToVacancies(boolean useAi, boolean forceMessage) {
        String resumeId = resumeService.getResume().getResumeId();
        List<VacancyItem> allVacancies = getAllVacancies(resumeId);

        removeNegotiationVacancies(allVacancies);

        for (VacancyItem vacancy : allVacancies) {
            try {
                if (vacancy.has_test() || vacancy.archived()) {
                    log.info("Skipping vacancy with test or archived: {}", vacancy.name());
                    continue;
                }
                String message = "";
                if (forceMessage || vacancy.response_letter_required()) {
                    message = useAi
                            ? blackboxChatClient.generateMessage(properties.getPrePrompt() + "\n\n" + vacancy.name())
                            : cover_letter;
                }
                if (properties.isDryRun()) {
                    log.info("Dry run mode: application would be sent for vacancy {}", vacancy.name());
                } else {
                    float delay = randomInterval(properties.getApplyIntervalMin(), properties.getApplyIntervalMax());
                    Thread.sleep((long) (delay * 1000));

                    requestTemplates.postDataToRequest(resumeId, vacancy.id(), message);

                    log.info("Applied to vacancy: {}", vacancy.name());
                }
            } catch (Exception e) {
                log.error("Failed to apply to vacancy: {}", vacancy.name(), e);
            }
        }
    }

    private List<VacancyItem> getAllVacancies(String resumeId) {
        List<VacancyItem> all = new ArrayList<>();
        int perPage = 100;   // количество вакансий на одной странице
        int totalPages = (int) Math.ceil((double) countVacancies / perPage);
        for (int page = 0; page < totalPages; page++) {
            int vacanciesOnPage = Math.min(perPage, countVacancies - page * perPage);

//            ApiListResponse<VacancyItem> response = vacancyClient.getSearchVacancies(resumeId, page, vacanciesOnPage);
            ApiListResponse<VacancyItem> response = vacancyClient.getSearchVacancies2(page, vacanciesOnPage);

            all.addAll(response.items());
            try {
                float delay = randomInterval(properties.getPageIntervalMin(), properties.getPageIntervalMax());
                Thread.sleep((long) (delay * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.info("All vacancies size: {}", all.size());
        return all;
    }

    private void removeNegotiationVacancies(List<VacancyItem> allVacancies) {
        List<String> list = negotiationsAll.getNegotiationList();
        Set<String> negotiationIds = new HashSet<>(list);
        allVacancies.removeIf(vacancy -> negotiationIds.contains(vacancy.id()));
    }

    private float randomInterval(float min, float max) {
        return min + new Random().nextFloat() * (max - min);
    }
}
