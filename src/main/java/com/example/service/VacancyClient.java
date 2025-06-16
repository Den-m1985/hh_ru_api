package com.example.service;

import com.example.dto.VacancyRequest;
import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.model.User;
import com.example.service.common.UserService;
import com.example.service.mapper.VacancyRequestMapper;
import com.example.util.HeadHunterProperties;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacancyClient {
    private final RequestTemplates requestTemplates;
    private final HeadHunterProperties headHunterProperties;
    private final UserService userService;

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Poisk-vakansij-dlya-soiskatelya/operation/get-vacancies-similar-to-resume">...</a>
     */
    public ApiListResponse<VacancyItem> getSimilarVacancies(VacancyRequest vacancyRequest, int page, int perPage) {
        Map<String, String> params = VacancyRequestMapper.toParamMap(vacancyRequest, page, perPage);
        String url = createSimilarUrl(vacancyRequest.resumeId(), params);
        log.info("Fetching similar vacancies from: {}", url);
        User user = userService.findUserByResume(vacancyRequest.resumeId());
        return requestTemplates.getDataFromRequest(url, user.getHhToken());
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Poisk-vakansij/operation/get-vacancies">...</a>
     */
    public ApiListResponse<VacancyItem> getSearchVacancies(VacancyRequest vacancyRequest, int page, int perPage) {
        Map<String, String> params = VacancyRequestMapper.toParamMap(vacancyRequest, page, perPage);
        String url = createSearchUrl(params);
        log.info("Fetching similar vacancies from: {}", url);
        User user = userService.findUserByResume(vacancyRequest.resumeId());
        return requestTemplates.getDataFromRequest(url, user.getHhToken());
    }

    private String createSimilarUrl(String resumeId, Map<String, String> search) {
        String url = String.format("%s/resumes/%s/similar_vacancies?", headHunterProperties.getBaseUrlApi(), resumeId);
        return url + buildUrl(search);
    }

    private String createSearchUrl(Map<String, String> search) {
        String url = String.format("%s/vacancies?", headHunterProperties.getBaseUrlApi());
        return url + buildUrl(search);
    }

    public String buildUrl(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!sb.isEmpty()) sb.append("&");
            sb.append(URLEncoder.encode(entry.getKey(), UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), UTF_8));
        }
        return sb.toString();
    }
}
