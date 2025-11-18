package com.example.mapper;

import com.example.dto.company.CompanyResponseDto;
import com.example.model.Recruiter;
import com.example.model.it_map.Company;
import com.example.model.it_map.CompanyCategory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class CompanyMapper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Company toEntity(CompanyResponseDto dto, Set<CompanyCategory> category, List<Recruiter> recruiters) {
        Company company = new Company();
        company.setCategories(category);
        company.setName(dto.getName());
        company.setCareerUrl(dto.getCareerUrl());
        company.setLogoPath(dto.getLogoUrl());
        company.setRecruiter(recruiters);
        company.setCompanyUrl(dto.getCompanyUrl());
        return company;
    }

    public CompanyResponseDto toDto(Company entity) {
        return new CompanyResponseDto(
                entity.getId(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().format(formatter) : null,
                entity.getUpdatedAt() != null ? entity.getCreatedAt().format(formatter) : null,
                convertToList(entity.getCategories()),
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

    private List<Integer> convertToList(Set<CompanyCategory> categories) {
        List<Integer> arrayList = new ArrayList<>();
        categories.forEach(cat -> arrayList.add(cat.getId()));
        return arrayList;
    }
}
