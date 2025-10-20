package com.example.controller.it_map;

import com.example.controller.interfaces.CompanyApi;
import com.example.dto.company.CompanyResponseDto;
import com.example.service.it_map.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/it_map/companies")
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

    @PostMapping(value = "/add/with-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyResponseDto> addCompany(
            @RequestPart("companyData") CompanyResponseDto response,
            @RequestPart(value = "logoFile") MultipartFile logoFile
    ) {
        CompanyResponseDto updatedCompany = companyService.addCompany(response, logoFile);
        return ResponseEntity.ok(updatedCompany);
    }

    @PostMapping("/{companyId}/logo")
    public ResponseEntity<CompanyResponseDto> uploadLogo(
            @PathVariable Integer companyId,
            @RequestPart("File") MultipartFile file
    ) {
        CompanyResponseDto updatedCompany = companyService.updateCompanyLogo(companyId, file);
        return ResponseEntity.ok(updatedCompany);
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
