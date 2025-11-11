package com.example.service.superjob;

import com.example.dto.VacancyHistoryDto;
import com.example.dto.superjob.SendCvOnVacancyResponse;
import com.example.dto.superjob.SuperjobResumeDto;
import com.example.dto.superjob.SuperjobVacancyRequest;
import com.example.dto.superjob.Vacancy;
import com.example.dto.superjob.VacancyResponse;
import com.example.dto.superjob.resume.ResumeObject;
import com.example.dto.superjob.resume.SuperJobResumeResponse;
import com.example.model.SuperjobResume;
import com.example.model.SuperjobToken;
import com.example.model.User;
import com.example.service.VacancyHistoryService;
import com.example.service.common.UserService;
import com.example.service.notify.NotificationService;
import com.example.util.RequestTemplates;
import com.example.util.SuperjobProperties;
import com.example.util.SuperjobVacancyUrlBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientSuperjob {
    NotificationService notificationService;
    RequestTemplates requestTemplates;
    SuperjobTokenService superjobTokenService;
    SuperjobProperties superjobProperties;
    SuperjobResumeService superjobResumeService;
    UserService userService;
    SuperjobVacancyFilter superjobVacancyFilter;
    SuperjobVacancyUrlBuilder superjobVacancyUrlBuilder;
    VacancyHistoryService historyService;

    public void sendCvOnVacancy(SuperjobVacancyRequest request, User user) {
        SuperjobToken superjobToken = superjobTokenService.getTokenFromDb(user);
        List<Vacancy> filtered = getFilteredVacancy(user, request);
        String url = superjobProperties.baseUrlApi() + "/2.0/send_cv_on_vacancy/";
        List<String> reportList = new ArrayList<>();
        for (Vacancy vacancy : filtered) {
            Map<String, String> formData = Map.of(
                    "id_cv", String.valueOf(request.resumeId()),
                    "id_vacancy", String.valueOf(vacancy.id()),
                    "comment", request.coverLetter()
            );
            String body = formData.entrySet().stream()
                    .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
            try {
                SendCvOnVacancyResponse response = requestTemplates.postRequestToSuperjob(url, superjobToken, body);
                if (response.result() != null && response.result()) {
                    reportList.add("✅ откликнулся на вакансию: " + vacancy.external_url());
                    historyService.addHistory(new VacancyHistoryDto(null, "sj", vacancy.external_url(), null), user);
                } else if (response.error() != null) {
                    log.error("Ошибка: {} - {}*****  вакансия: {} {}", response.error().code(), response.error().message(), vacancy.id(), vacancy.external_url());
                    reportList.add("❌ не смог откликнутся на вакансию: " + vacancy.external_url());
                }
            } catch (Exception e) {
                log.error("❌ Failed to apply to vacancy: {}", vacancy.profession(), e);
                Thread.currentThread().interrupt();
            }
        }
        notifyUser(user, reportList);
    }

    private List<Vacancy> getFilteredVacancy(User user, SuperjobVacancyRequest request) {
        Set<Vacancy> allVacancy = getVacancySuperjob(user, request);
        List<Vacancy> filtered = superjobVacancyFilter.filteredVacancies(allVacancy, request.keywordsToExclude());
        log.info("Superjob, after feltered {} vacancies", filtered.size());
        return filtered;
    }

    private void notifyUser(User user, List<String> reportList) {
        String message;
        if (!reportList.isEmpty()) {
            message = "отчет откликов на вакансии superjob:\n\n" + reportList;
        } else {
            message = "отчет пуст по отклику на сервисе superjob";
        }
        notificationService.notifyUser(user.getId(), message);
    }

    /**
     * <a href="https://api.superjob.ru/#:~:text=%D0%B0%D0%B2%D1%82%D0%BE%D1%80%D0%B8%D0%B7%D0%B0%D1%86%D0%B8%D0%B8%20%D0%BF%D0%BE%D0%B4%20%D1%81%D0%BE%D0%B8%D1%81%D0%BA%D0%B0%D1%82%D0%B5%D0%BB%D0%B5%D0%BC.-,%D0%9C%D0%B5%D1%82%D0%BE%D0%B4%D1%8B%20API,%D0%9F%D0%BE%D0%B8%D1%81%D0%BA%20%D0%B2%D0%B0%D0%BA%D0%B0%D0%BD%D1%81%D0%B8%D0%B9,-Resource%20information">...</a>
     */
    public Set<Vacancy> getVacancySuperjob(User user, SuperjobVacancyRequest request) {
        VacancyResponse array = getSearchVacanciesSuperjob(user, request);
        Set<Vacancy> all = new HashSet<>(array.objects());
        log.info("Superjob, find {} vacancies", all.size());
        return all;
    }

    public VacancyResponse getSearchVacanciesSuperjob(User user, SuperjobVacancyRequest request) {
        String url = superjobVacancyUrlBuilder.buildUrl(request.searchRequest(), superjobProperties.baseUrlApi());
        return requestTemplates.getRequestToSuperjob(url, user.getSuperjobToken());
    }

    @Transactional
    public List<SuperjobResumeDto> getResumeItemsDto(User user) {
        List<SuperjobResume> existingResumes = getResumeFromSuperjob(user);
        List<SuperjobResumeDto> result = new ArrayList<>();
        for (SuperjobResume resume : existingResumes) {
            SuperjobResumeDto resumeItemDto = new SuperjobResumeDto(resume.getResumeTitle(), resume.getResumeId());
            result.add(resumeItemDto);
        }
        return result;
    }

    public List<SuperjobResume> getResumeFromSuperjob(User user) {
        String url = superjobProperties.baseUrlApi() + "/2.0/user_cvs/";
        SuperJobResumeResponse data = requestTemplates.getResumes(url, user.getSuperjobToken());
        user = userService.getUserById(user.getId());

        List<SuperjobResume> existingResumes = user.getSuperjobResumes();
        if (existingResumes != null) {
            superjobResumeService.deleteAll(existingResumes);
            existingResumes.clear();
        } else {
            existingResumes = new ArrayList<>();
        }
        for (ResumeObject resume : data.objects()) {
            SuperjobResume newResume = new SuperjobResume();
            newResume.setResumeTitle(resume.name());
            newResume.setResumeId(resume.id());
            newResume.setUser(user);
            existingResumes.add(newResume);
        }
        return superjobResumeService.saveAll(existingResumes);
    }
}
