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
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyClient vacancyClient;
    private final GenerateChatClient blackboxChatClient;
    private final ApplicationProperties properties;
    private final ResumeService resumeService;
    private final RequestTemplates requestTemplates;
    private final VacancyFilter vacancyFilter;

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
        allVacancies.stream().forEach(o -> {
            System.out.println(o.name());
            System.out.println(o.published_at());
        });
        System.out.println();
        List<VacancyItem> filtered = vacancyFilter.filterVacancies(allVacancies);
        System.out.println(filtered.size());
        filtered.stream().forEach(o -> System.out.println(o.name()));

        for (VacancyItem vacancy : filtered) {
            try {
                String message = "";
//                if (forceMessage || vacancy.response_letter_required()) {
//                    message = useAi
//                            ? blackboxChatClient.generateMessage(properties.getPrePrompt() + "\n\n" + vacancy.name())
//                            : cover_letter;
//                }
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
        int maxPerPage = 100;   // количество вакансий на одной странице
        int totalPages = (int) Math.ceil((double) countVacancies / maxPerPage);
        for (int page = 0; page < totalPages; page++) {
            int vacanciesOnPage = Math.min(maxPerPage, countVacancies - page * maxPerPage);

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

    private float randomInterval(float min, float max) {
        return min + new Random().nextFloat() * (max - min);
    }
}
