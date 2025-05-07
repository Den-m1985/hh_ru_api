package com.example.dto.vacancy_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResumeItemDto(
        // ...
        String id
        // ...

) {
}
