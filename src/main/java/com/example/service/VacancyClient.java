package com.example.service;

import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.model.User;
import com.example.service.common.UserService;
import com.example.util.HeadHunterProperties;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
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
    // TODO получать параметры из запроса
    public ApiListResponse<VacancyItem> getSimilarVacancies(String resumeId, int page, int perPage) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(perPage));
        params.put("order_by", "relevance");
        params.put("text", "Java");
        params.put("vacancy_search_fields", "name");
        params.put("experience", "between1And3");
        String url = createSimilarUrl(resumeId, params);
        log.info(url);
        User user = userService.findUserByResume(resumeId);
        return requestTemplates.getDataFromRequest(url, user.getHhToken());
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Poisk-vakansij/operation/get-vacancies">...</a>
     */
    // TODO получать параметры из запроса
    public ApiListResponse<VacancyItem> getSearchVacancies(String resumeId, int page, int perPage) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(perPage));
//        params.put("experience", "between1And3");
        params.put("experience", "between3And6");
//        params.put("order_by", "relevance");
        params.put("order_by", "publication_time");  // sort по дате
        params.put("text", "Java");
        params.put("search_field", "name");
        String url = createSearchUrl(params);
        log.info(url);
        User user = userService.findUserByResume(resumeId);
        return requestTemplates.getDataFromRequest(url, user.getHhToken());
    }

    private String createSimilarUrl(String resumeId, Map<String, String> search) {
        String url = String.format("%s/resumes/%s/similar_vacancies?", headHunterProperties.getBaseUrlApi(), resumeId);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : search.entrySet()) {
            if (!sb.isEmpty()) sb.append("&");
            sb.append(URLEncoder.encode(entry.getKey(), UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), UTF_8));
        }
        return url + sb;
    }

    private String createSearchUrl(Map<String, String> search) {
        String url = String.format("%s/vacancies?", headHunterProperties.getBaseUrlApi());
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : search.entrySet()) {
            if (!sb.isEmpty()) sb.append("&");
            sb.append(URLEncoder.encode(entry.getKey(), UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), UTF_8));
        }
        return url + sb;
    }
}
