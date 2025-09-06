package com.example.mapper;

import com.example.dto.company.CompanyResponseDto;
import com.example.model.Company;
import com.example.model.CompanyCategory;
import com.example.model.Recruiter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompanyMapper {

    public Company toEntity(CompanyResponseDto dto, CompanyCategory category, List<Recruiter> recruiters) {
        Company company = new Company();
        company.setCategory(category);
        company.setName(dto.name());
        company.setCareerUrl(dto.careerUrl());
        company.setRecruiter(recruiters);
        return company;
    }

    public CompanyResponseDto toDto(Company entity) {
        return new CompanyResponseDto(
                entity.getCategory().getName(),
                entity.getName(),
                entity.getCompanyUrl(),
                entity.getCareerUrl(),
                entity.getRecruiter().stream()
                        .map(Recruiter::getId)
                        .toList()
        );
    }

    public List<CompanyResponseDto> toDto(List<Company> entities) {
        return entities.stream().map(entity ->
                new CompanyResponseDto(
                        entity.getCategory().getName(),
                        entity.getName(),
                        entity.getCompanyUrl(),
                        entity.getCareerUrl(),
                        entity.getRecruiter().stream()
                                .map(Recruiter::getId)
                                .toList())
        ).toList();
    }

}
