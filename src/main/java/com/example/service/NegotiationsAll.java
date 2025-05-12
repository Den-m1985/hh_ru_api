package com.example.service;

import com.example.dto.NegotiationItemDto;
import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.util.HeadHunterProperties;
import com.example.util.RequestTemplates;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public class NegotiationsAll {
    private final RequestTemplates requestTemplates;
    private final HeadHunterProperties headHunterProperties;
    @Getter
    private final List<String> negotiationList = new ArrayList<>();

    public void getNegotiations() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("page", String.valueOf(0));
        params.put("per_page", String.valueOf(1000));
        params.put("order_by", "created_at");
        params.put("status", "all");
        String url = createUrl(params);
        System.out.println(url);
        ApiListResponse<NegotiationItemDto> response = requestTemplates.getDataFromRequest2(url);
        System.out.println(response.items().size());
        for (NegotiationItemDto str : response.items()) {
            negotiationList.add(str.vacancy().id());
        }
        log.info("negotiation size: {}", response.items().size());
    }

    private String createUrl(Map<String, String> search) {
        String url = String.format("%s/negotiations?", headHunterProperties.getBaseUrlApi());
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
