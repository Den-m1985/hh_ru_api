package com.example.service;

import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.util.HeadHunterProperties;
import com.example.util.RequestTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class VacancyClient {
    private final RequestTemplates requestTemplates;
    private final ResumeService resumeService;
    private final HeadHunterProperties headHunterProperties;

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Poisk-vakansij-dlya-soiskatelya/operation/get-vacancies-similar-to-resume">...</a>
     */
    public ApiListResponse<VacancyItem> getSimilarVacancies(String resumeId, int page, int perPage, String orderBy, String search) {
        String url = createUrl(resumeId, page, perPage, orderBy, search);
        return requestTemplates.getDataFromRequest(url);
    }

    public void test() {
        String resumeId = resumeService.getResume().getResumeId();
        ApiListResponse<VacancyItem> dfg = getSearchVacancies(resumeId, 1, 5);
        System.out.println(dfg.items().size());
        for (VacancyItem vacancyItem : dfg.items()) {
            System.out.println(vacancyItem.name());
            System.out.println(vacancyItem.experience() + "\n");
        }
    }

    public ApiListResponse<VacancyItem> getSearchVacancies(String resumeId, int page, int perPage) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(perPage));
        params.put("order_by", "relevance");
        params.put("text", "Java");
        params.put("vacancy_search_fields", "name");
        params.put("experience", "between1And3");
        String url = createUrl2(resumeId, params);
        System.out.println(url);
        return requestTemplates.getDataFromRequest(url);
    }

    // https://hh.ru/search/vacancy?area=113&enable_snippets=true&experience=between1And3&no_magic=true&text=Java
    public ApiListResponse<VacancyItem> getSearchVacancies2(int page, int perPage) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("page", String.valueOf(page));
        params.put("per_page", String.valueOf(perPage));
//        params.put("enable_snippets", "true");
        params.put("experience", "between1And3");
        params.put("no_magic", "true");
        params.put("text", "Java");
        params.put("vacancy_search_fields", "name");
        String url = createUrl3(params);
        System.out.println(url);
        return requestTemplates.getDataFromRequest(url);
    }

    private String createUrl(String resumeId, int page, int perPage, String orderBy, String search) {
        String url = String.format("%s/resumes/%s/similar_vacancies?page=%d&per_page=%d&order_by=%s", headHunterProperties.getBaseUrlApi(), resumeId, page, perPage, orderBy);
        if (search != null) {
            url += "&text=" + search;
        }
        return url;
    }

    private String createUrl2(String resumeId, Map<String, String> search) {
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

    private String createUrl3(Map<String, String> search) {
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
