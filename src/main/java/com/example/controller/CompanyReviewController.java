package com.example.controller;

import com.example.dto.agregator_dto.CompaniesProfileRequest;
import com.example.dto.agregator_dto.CompaniesProfileResponse;
import com.example.service.aggregator.CompaniesProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/interviews")
public class CompanyReviewController {
    private final CompaniesProfileService companiesProfileService;

    @PostMapping("/filter")
    public ResponseEntity<CompaniesProfileResponse> getCompaniesProfile(@RequestBody CompaniesProfileRequest request) {
        return ResponseEntity.ok(companiesProfileService.getCompaniesProfile(request));
    }
}
