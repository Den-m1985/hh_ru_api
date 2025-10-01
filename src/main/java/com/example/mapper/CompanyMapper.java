package com.example.mapper;

import com.example.dto.company.CompanyResponseDto;
import com.example.model.Company;
import com.example.model.CompanyCategory;
import com.example.model.Recruiter;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CompanyMapper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Company toEntity(CompanyResponseDto dto, CompanyCategory category, List<Recruiter> recruiters, String logofile) {
        Company company = new Company();
        company.setCategory(category);
        company.setName(dto.name());
        company.setCareerUrl(dto.careerUrl());
        company.setLogoPath(logofile);
        company.setRecruiter(recruiters);
        return company;
    }

    public CompanyResponseDto toDto(Company entity) {
        return new CompanyResponseDto(
                entity.getId(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().format(formatter) : null,
                entity.getUpdatedAt() != null ? entity.getCreatedAt().format(formatter) : null,
                entity.getCategory().getName(),
                entity.getName(),
                entity.getCompanyUrl(),
                entity.getCareerUrl(),
                entity.getLogoPath(),
                entity.getRecruiter().stream()
                        .map(Recruiter::getId)
                        .toList()
        );
    }

    public List<CompanyResponseDto> toDto(List<Company> entities) {
        return entities.stream().map(this::toDto).toList();
    }

}
