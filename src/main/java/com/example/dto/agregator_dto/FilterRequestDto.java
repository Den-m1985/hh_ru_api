package com.example.dto.agregator_dto;

import com.example.enums.FilterMode;
import com.example.enums.FilterType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record FilterRequestDto(

        @JsonProperty("search_field")
        String searchField,

        FilterMode mode,

        Set<FilterType> filters
) {
}
