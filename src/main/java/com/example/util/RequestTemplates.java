package com.example.util;

import com.example.dto.HhTokenResponse;
import com.example.dto.NegotiationItemDto;
import com.example.dto.SavedSearchDto;
import com.example.dto.superjob.SendCvOnVacancyResponse;
import com.example.dto.superjob.SuperjobTokenResponse;
import com.example.dto.superjob.VacancyResponse;
import com.example.dto.superjob.resume.SuperJobResumeResponse;
import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.Area;
import com.example.dto.vacancy_dto.ResumeDto;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.model.HhToken;
import com.example.model.SuperjobToken;
import com.example.model.User;
import com.example.service.common.CreateHeaders;
import com.example.service.common.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestTemplates {
    private final HttpUtils httpUtils;
    private final CreateHeaders createHeaders;
    private final UserService userService;
    private final HeadHunterProperties headHunterProperties;

    public ResumeDto getMineResume(HhToken hhToken) {
        return httpUtils.safeRequest(
                headHunterProperties.baseUrlApi + "/resumes/mine",
                HttpMethod.GET,
                createHeaders.getHeaders(hhToken),
                null,
                new TypeReference<>() {
                }
        );
    }

    public SuperJobResumeResponse getResumes(String url, SuperjobToken token) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.GET,
                createHeaders.createHeadersSuperjob(token),
                null,
                new TypeReference<>() {
                }
        );
    }

    public List<Area> getAreaFromRequest(String url, HhToken hhToken) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.GET,
                createHeaders.getHeaders(hhToken),
                null,
                new TypeReference<>() {
                }
        );
    }

    public ApiListResponse<VacancyItem> getDataFromRequest(String url, HhToken hhToken) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.GET,
                createHeaders.getHeaders(hhToken),
                null,
                new TypeReference<>() {
                }
        );
    }

    public VacancyResponse getRequestToSuperjob(String url, SuperjobToken token) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.GET,
                createHeaders.createHeadersSuperjob(token),
                null,
                new TypeReference<>() {
                }
        );
    }

        public SendCvOnVacancyResponse postRequestToSuperjob(String url, SuperjobToken token, String body) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.POST,
                createHeaders.createHeadersSuperjob(token),
                body,
                new TypeReference<>() {
                }
        );
    }

    public HhTokenResponse getHhTokenFromRequest(String url) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.POST,
                createHeaders.createHeadersForToken(),
                null,
                new TypeReference<>() {
                }
        );
    }

    public SuperjobTokenResponse getSuperjobTokenFromRequest(String url) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.POST,
                createHeaders.createHeadersForToken(),
                null,
                new TypeReference<>() {
                }
        );
    }

    public ApiListResponse<NegotiationItemDto> getDataFromRequest2(String url, User user) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.GET,
                createHeaders.getHeaders(user.getHhToken()),
                null,
                new TypeReference<>() {
                }
        );
    }

    public ApiListResponse<SavedSearchDto> getSavedSearches(String url, User user) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.GET,
                createHeaders.getHeaders(user.getHhToken()),
                null,
                new TypeReference<>() {
                }
        );
    }

    /**
     * <a href="https://api.hh.ru/openapi/redoc#tag/Vakansii/operation/apply-to-vacancy">...</a>
     */
    // TODO сделать form-data как-то проще, надо перейти на WebClient. HttpClient неудобен для multipart
    public String postDataToRequest(String resumeId, String vacancyId, String message) {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String CRLF = "\r\n";

        StringBuilder formData = new StringBuilder();
        formData.append("--").append(boundary).append(CRLF);
        formData.append("Content-Disposition: form-data; name=\"message\"").append(CRLF).append(CRLF);
        formData.append(message != null ? message : "").append(CRLF);

        formData.append("--").append(boundary).append(CRLF);
        formData.append("Content-Disposition: form-data; name=\"resume_id\"").append(CRLF).append(CRLF);
        formData.append(resumeId).append(CRLF);

        formData.append("--").append(boundary).append(CRLF);
        formData.append("Content-Disposition: form-data; name=\"vacancy_id\"").append(CRLF).append(CRLF);
        formData.append(vacancyId).append(CRLF);

        formData.append("--").append(boundary).append("--").append(CRLF);

        String body = formData.toString();
        User user = userService.findUserByResume(resumeId);
        Map<String, String> headers = new HashMap<>(createHeaders.getHeaders(user.getHhToken()));
        headers.put("Content-Type", "multipart/form-data; boundary=" + boundary);

        return httpUtils.safeRequest(
                headHunterProperties.baseUrlApi + "/negotiations",
                HttpMethod.POST,
                headers,
                body,
                new TypeReference<>() {
                }
        );
    }
}
