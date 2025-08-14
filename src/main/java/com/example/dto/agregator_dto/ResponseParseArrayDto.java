package com.example.dto.agregator_dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResponseParseArrayDto(

        @JsonProperty("name_site")
        String nameSite,

        @JsonProperty("parse_items")
        List<AggregatorResponseDto> parseItems
) {
}
