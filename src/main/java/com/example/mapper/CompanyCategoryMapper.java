package com.example.mapper;

import com.example.dto.agregator_dto.CompanyCategoryDto;
import com.example.model.it_map.Company;
import com.example.model.it_map.CompanyCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CompanyCategoryMapper {

    public CompanyCategory toEntity(CompanyCategoryDto dto, Set<Company> companies) {
        CompanyCategory companyCategory = new CompanyCategory();
        companyCategory.setCompanies(companies);
        companyCategory.setName(dto.name());
        companyCategory.setDescription(dto.description());
        companyCategory.setId(dto.id());
        return companyCategory;
    }

    public CompanyCategoryDto toDto(CompanyCategory newCategory) {
        return new CompanyCategoryDto(
                newCategory.getId(),
                newCategory.getName(),
                newCategory.getDescription()
        );
    }

    public List<CompanyCategoryDto> toDto(List<CompanyCategory> entities) {
        return entities.stream().map(this::toDto).toList();
    }
}
