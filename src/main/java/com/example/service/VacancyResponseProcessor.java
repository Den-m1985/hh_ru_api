package com.example.service;

import com.example.dto.FilterResult;
import com.example.dto.VacancyRequest;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.service.notify.NotificationService;
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
    private final NotificationService notificationService;

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Vakansii/operation/apply-to-vacancy">...</a>
     */
    public void respondToRelevantVacancies(VacancyRequest request, Integer userId) {
        List<VacancyItem> filtered = prepareData(request, userId);
        for (VacancyItem vacancy : filtered) {
            try {
                String message = coverLetterService.prepareMessage(vacancy, request.coverLetter());
                applicationService.sendResponseToVacancy(request, vacancy, message);
                notificationService.notifyUser(userId, "Отклик на: " + vacancy.name() + " в компанию: " + vacancy.employer().name());
            } catch (Exception e) {
                log.error("❌ Failed to apply to vacancy: {}", vacancy.name(), e);
                Thread.currentThread().interrupt();
            }
        }
        log.info("✅ Finished apply to vacancies with resume id: {}", request.resumeId());
        notificationService.notifyUser(userId, "✅ Finished apply to vacancies");
    }

    public List<VacancyItem> prepareData(VacancyRequest request, Integer userId) {
        Set<VacancyItem> allVacanciesFromServer = allVacancies.getAllVacancies(request);
        FilterResult filteredData = vacancyFilter.filterVacancies(allVacanciesFromServer, request.keywordsToExclude());
        log.info("All vacancies size: {} Filtered vacancies size: {}, with test {}, according to resume id: {}",
                allVacanciesFromServer.size(),
                filteredData.filteredVacancies().size(),
                filteredData.vacanciesWithTests().size(),
                request.resumeId());
        sendTestVacancies(filteredData.vacanciesWithTests(), userId);
        return filteredData.filteredVacancies();
    }

    private void sendTestVacancies(List<VacancyItem> vacanciesWithTests, Integer userId) {
        for (VacancyItem vacancyItem : vacanciesWithTests) {
            String message = "Вакансия с тестом: \n\n" + vacancyItem.alternate_url();
            notificationService.notifyUser(userId, message);
        }
    }
}
