package com.example.service.aggregator;

import com.example.dto.agregator_dto.CompanyCategoryDto;
import com.example.model.CompanyCategory;
import com.example.repository.CompanyCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CompanyCategoryService {
    private final CompanyCategoryRepository categoryRepository;

    public CompanyCategoryDto getCategoryByName(String nameCategory) {
        CompanyCategory category = categoryRepository.findByName(nameCategory)
                .orElseThrow(() -> new EntityNotFoundException("Company_category with name: " + nameCategory + " not found"));
        return new CompanyCategoryDto(category.getId(), category.getName(), category.getDescription());
    }


    public List<CompanyCategoryDto> getAllCompaniesCategory() {
        List<CompanyCategory> companies = categoryRepository.findAll();
        return companies.stream()
                .map(entity -> new CompanyCategoryDto(entity.getId(), entity.getName(), entity.getDescription()))
                .toList();
    }

    public CompanyCategory getOrCreateCategory(String nameCategory) {
        Optional<CompanyCategory> category = categoryRepository.findByName(nameCategory);
        if (category.isEmpty()) {
            CompanyCategory newCategory = new CompanyCategory();
            newCategory.setName(nameCategory);
            return categoryRepository.save(newCategory);
        }
        return category.get();
    }

    public CompanyCategoryDto createCategory(CompanyCategoryDto category) {
        CompanyCategory newCategory = new CompanyCategory();
        newCategory.setName(category.name());
        newCategory.setDescription(category.description());
        newCategory = categoryRepository.save(newCategory);
        return new CompanyCategoryDto(newCategory.getId(), newCategory.getName(), newCategory.getDescription());
    }
}
