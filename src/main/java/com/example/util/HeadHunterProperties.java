package com.example.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
    String hhUserAgent;
    String coverLetter;
    Boolean forceCoverLetter;
    Boolean useAi;
}
