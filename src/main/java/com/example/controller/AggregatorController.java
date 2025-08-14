package com.example.controller;

import com.example.dto.agregator_dto.FilterRequestDto;
import com.example.dto.agregator_dto.ResponseParseArrayDto;
import com.example.enums.FilterMode;
import com.example.enums.FilterType;
import com.example.service.aggregator.AggregatorParseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1/aggregator")
@RequiredArgsConstructor
public class AggregatorController {
    private final AggregatorParseService aggregatorParseService;

    @GetMapping()
    public ResponseEntity<List<ResponseParseArrayDto>> getVacancies(/*FilterRequestDto filterRequestDto*/) {
        Set<FilterType> type = new HashSet<>();
        type.add(FilterType.EXPERIENCE);
        type.add(FilterType.BIG_TECH);
        FilterRequestDto filterRequestDto1 = new FilterRequestDto(
                "java",
                FilterMode.BROAD_SEARCH,
                type
        );
        return ResponseEntity.ok(aggregatorParseService.parseWithFilter(filterRequestDto1));
    }
}
