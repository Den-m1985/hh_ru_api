package com.example.controller;

import com.example.controller.interfaces.CompanyApi;
import com.example.dto.company.CompanyResponseDto;
import com.example.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class CompanyController implements CompanyApi {
    private final CompanyService companyService;

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable Integer id) {
        return ResponseEntity.ok(companyService.getCompanyDto(id));
    }

    @PostMapping("/add")
    public ResponseEntity<CompanyResponseDto> addCompany(@RequestBody CompanyResponseDto response) {
        return ResponseEntity.ok(companyService.addCompany(response));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CompanyResponseDto>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CompanyResponseDto>> getCompaniesByFilters(@RequestParam List<String> categories) {
        return ResponseEntity.ok(companyService.getCompaniesByCategories(categories));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Integer id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
