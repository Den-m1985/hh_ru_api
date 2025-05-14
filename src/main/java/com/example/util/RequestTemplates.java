package com.example.util;

import com.example.dto.NegotiationItemDto;
import com.example.dto.SavedSearchDto;
import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.ResumeDto;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.service.CreateHeaders;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestTemplates {
    private final HttpUtils httpUtils;
    private final CreateHeaders createHeaders;
    private final HeadHunterProperties headHunterProperties;

    public ResumeDto getMineResume() {
        return httpUtils.safeRequest(
                headHunterProperties.baseUrlApi + "/resumes/mine",
                HttpMethod.GET,
                createHeaders.getHeaders(),
                null,
                new TypeReference<>() {
                }
        );
    }

    public ApiListResponse<VacancyItem> getDataFromRequest(String url) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.GET,
                createHeaders.getHeaders(),
                null,
                new TypeReference<>() {
                }
        );
    }

    public ApiListResponse<NegotiationItemDto> getDataFromRequest2(String url) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.GET,
                createHeaders.getHeaders(),
                null,
                new TypeReference<>() {
                }
        );
    }

    public ApiListResponse<SavedSearchDto> getSavedSearches(String url) {
        return httpUtils.safeRequest(
                url,
                HttpMethod.GET,
                createHeaders.getHeaders(),
                null,
                new TypeReference<>() {
                }
        );
    }

    // TODO сделать form-data как-то проще
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

        Map<String, String> headers = new HashMap<>(createHeaders.getHeaders());
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
