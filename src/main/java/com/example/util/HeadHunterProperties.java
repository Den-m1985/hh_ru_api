package com.example.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "hh")
public class HeadHunterProperties {
    String clientId;
    String clientSecret;
    String redirectUri;
    String baseUrlApi;
    String baseUrl;
    Integer countVacancies;
    String hhUserAgent;
    String coverLetter;
    Boolean searchBySimilarVacancies;
    Boolean forceCoverLetter;
    Boolean useAi;
    List<String> keywordsToExclude;

    public void setKeywordsToExclude(String keywords) {
        this.keywordsToExclude = Arrays.stream(keywords.split(","))
                .map(String::trim)
                .toList();
    }
}
