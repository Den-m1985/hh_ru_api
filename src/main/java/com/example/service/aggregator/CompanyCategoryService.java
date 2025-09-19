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

    public CompanyCategory getCategoryByName(String nameCategory) {
        return categoryRepository.findByName(nameCategory)
                .orElseThrow(() -> new EntityNotFoundException("Company_category with name: " + nameCategory + " not found"));
    }

    public CompanyCategory getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company_category with id: " + id + " not found"));
    }

    public List<CompanyCategoryDto> getAllCompaniesCategory() {
        List<CompanyCategory> companies = categoryRepository.findAll();
        return companies.stream().map(this::mapperEntityToDto).toList();
    }

    public CompanyCategoryDto getCompanyCategoryDto(String nameCategory) {
        CompanyCategory category = getCategoryByName(nameCategory);
        return mapperEntityToDto(category);
    }

    public CompanyCategoryDto getCompanyCategoryDto(Integer id) {
        CompanyCategory category = getCategoryById(id);
        return mapperEntityToDto(category);
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
        return mapperEntityToDto(newCategory);
    }

    public CompanyCategoryDto mapperEntityToDto(CompanyCategory newCategory) {
        return new CompanyCategoryDto(
                newCategory.getId(),
                newCategory.getName(),
                newCategory.getDescription()
        );
    }

    public void deleteCategory(Integer id) {
        CompanyCategory companyCategory = getCategoryById(id);
        categoryRepository.delete(companyCategory);
    }
}
