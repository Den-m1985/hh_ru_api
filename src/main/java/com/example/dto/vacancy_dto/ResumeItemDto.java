package com.example.dto.vacancy_dto;

import com.example.dto.interfaces.BaseResumeDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResumeItemDto(

        String title,
        String id

) implements BaseResumeDto {
}
