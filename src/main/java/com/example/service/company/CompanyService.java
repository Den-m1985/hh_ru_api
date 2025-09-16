package com.example.service.company;

import com.example.dto.agregator_dto.CompaniesProfileRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Company getCompanyById(Integer id) {
        return companyRepository.findCompanyById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company with id: " + id + " not found"));
    }

    public Optional<Company> getOptionalCompanyById(Integer id) {
        return companyRepository.findCompanyById(id);
    }

    @Transactional
    public CompanyResponseDto addCompany(CompanyResponseDto dto) {
        CompanyCategory category = categoryService.getOrCreateCategory(dto.category());
        List<Recruiter> recruiters = new ArrayList<>();
        if (dto.recruiters() != null && !dto.recruiters().isEmpty()) {
            recruiters = recruiterRepository.findAllById(dto.recruiters());
        }
        Company company = companyMapper.toEntity(dto, category, recruiters);
        for (Recruiter recruiter : recruiters) {
            recruiter.setCompany(company);
        }
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

    public List<CompanyResponseDto> getCompaniesByCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
           return List.of();
        }
        List<Company> companies = companyRepository.findByCategoryNames(categories);
        return companies.stream()
                .map(companyMapper::toDto)
                .toList();
    }
}
