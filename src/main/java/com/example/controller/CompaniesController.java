package com.example.controller;

import com.example.dto.agregator_dto.AggregatorResponseDto;
import com.example.dto.agregator_dto.CompanyCategoryDto;
import com.example.dto.company.CompanyResponseDto;
import com.example.service.aggregator.CompaniesProfileService;
import com.example.service.aggregator.CompanyCategoryService;
import com.example.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/companies")
public class CompaniesController {
    private final CompanyService companyService;
    private final CompanyCategoryService categoryService;
    private final CompaniesProfileService companiesProfileService;


    @GetMapping("/{id}")
    public ResponseEntity<AggregatorResponseDto> getCompanyCard(@PathVariable Long id) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<CompanyResponseDto> addCompany(@RequestBody CompanyResponseDto response) {
        return ResponseEntity.ok(companyService.addCompany(response));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CompanyResponseDto>> getAllCompany() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CompanyResponseDto>> getCompaniesByFilters(@RequestParam List<String> categories) {
        return ResponseEntity.ok(companyService.getCompaniesByCategories(categories));
    }

    @PostMapping("/category/add")
    public ResponseEntity<CompanyCategoryDto> addCompanyCategory(@RequestBody CompanyCategoryDto response) {
        return ResponseEntity.ok(categoryService.createCategory(response));
    }

    @GetMapping("/category/get_by_name/{name}")
    public ResponseEntity<CompanyCategoryDto> getCompanyCategory(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }

    @GetMapping("/category/all")
    public ResponseEntity<List<CompanyCategoryDto>> getAllCompanyCategory() {
        return ResponseEntity.ok(categoryService.getAllCompaniesCategory());
    }
}
