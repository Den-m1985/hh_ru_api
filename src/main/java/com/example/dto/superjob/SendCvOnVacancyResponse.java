package com.example.dto.superjob;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SendCvOnVacancyResponse(
        Boolean result,

        @JsonProperty("error")
        ErrorInfo error
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ErrorInfo(
            Integer code,
            String message
    ) {
    }
}
