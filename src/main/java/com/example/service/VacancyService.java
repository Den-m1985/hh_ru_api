package com.example.service;

import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.util.ApplicationProperties;
import com.example.util.HeadHunterProperties;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final VacancyFilter vacancyFilter;
    private final HeadHunterProperties headHunterProperties;
    private final AllVacancies allVacancies;
    private final Random random = new Random();

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Vakansii/operation/apply-to-vacancy">...</a>
     */
    public void applyToVacancies(boolean useAi, boolean forceMessage) {
        String resumeId = resumeService.getResume().getResumeId();
        Set<VacancyItem> allVacanciesFromServer = allVacancies.getAllVacancies(resumeId);
        allVacanciesFromServer.stream().forEach(o -> {
            System.out.println(o.name());
            System.out.println(o.published_at());
        });
        System.out.println();
        List<VacancyItem> filtered = vacancyFilter.filterVacancies(allVacanciesFromServer);
        System.out.println(filtered.size());
        filtered.stream().forEach(o -> System.out.println(o.name()));

        for (VacancyItem vacancy : filtered) {
            try {
                String message = "";
                if (forceMessage || Boolean.TRUE.equals(vacancy.response_letter_required())) {
                    message = useAi
                            ? blackboxChatClient.generateMessage(properties.getPrePrompt() + "\n\n" + vacancy.name())
                            : headHunterProperties.getCoverLetter();
                }
                if (properties.isDryRun()) {
                    log.info("Dry run mode: application would be sent for vacancy {}", vacancy.name());
                } else {
                    float delay = randomInterval(properties.getApplyIntervalMin(), properties.getApplyIntervalMax());
                    Thread.sleep((long) (delay * 1000));

                    requestTemplates.postDataToRequest(resumeId, vacancy.id(), message);

                    log.info("Applied to vacancy: {}", vacancy.name());
                }
            } catch (InterruptedException e) {
                log.error("Failed to apply to vacancy: {}", vacancy.name(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private float randomInterval(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
}
