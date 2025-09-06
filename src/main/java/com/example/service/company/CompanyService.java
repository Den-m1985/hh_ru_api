package com.example.service.company;

import com.example.dto.company.CompanyResponseDto;
import com.example.mapper.CompanyMapper;
import com.example.model.Company;
import com.example.model.CompanyCategory;
import com.example.model.Recruiter;
import com.example.repository.CompanyRepository;
import com.example.repository.RecruiterRepository;
import com.example.service.aggregator.CompanyCategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyCategoryService categoryService;
    private final RecruiterRepository recruiterRepository;
    private final CompanyMapper companyMapper;

    public Company getCompanyByName(String name) {
        return companyRepository.findCompanyByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Company with name: " + name + " not found"));
    }

    public CompanyResponseDto addCompany(CompanyResponseDto dto) {
        CompanyCategory category = categoryService.getOrCreateCategory(dto.category());
        List<Recruiter> recruiters = new ArrayList<>();
        if (dto.recruiter() != null && !dto.recruiter().isEmpty()) {
            recruiters = recruiterRepository.findAllById(dto.recruiter());
        }
        Company company = companyMapper.toEntity(dto, category, recruiters);
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }

    Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public List<CompanyResponseDto> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companyMapper.toDto(companies);
    }
}
