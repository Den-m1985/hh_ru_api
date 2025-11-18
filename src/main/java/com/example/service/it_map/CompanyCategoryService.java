package com.example.service.it_map;

import com.example.dto.agregator_dto.CompanyCategoryDto;
import com.example.mapper.CompanyCategoryMapper;
import com.example.model.it_map.CompanyCategory;
import com.example.repository.it_map.CompanyCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyCategoryService {
    CompanyCategoryRepository categoryRepository;
    CompanyCategoryMapper categoryMapper;

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
        return categoryMapper.toDto(companies);
    }

    public CompanyCategoryDto getCompanyCategoryDto(String nameCategory) {
        CompanyCategory category = getCategoryByName(nameCategory);
        return categoryMapper.toDto(category);
    }

    public CompanyCategoryDto getCompanyCategoryDto(Integer id) {
        CompanyCategory category = getCategoryById(id);
        return categoryMapper.toDto(category);
    }

    public Set<CompanyCategory> getAllCategoryByArray(List<Integer> nameCategories) {
        return new HashSet<>(categoryRepository.findAllById(nameCategories));
    }

    public CompanyCategoryDto createCategory(CompanyCategoryDto category) {
        CompanyCategory newCategory = new CompanyCategory();
        newCategory.setName(category.name());
        newCategory.setDescription(category.description());
        newCategory = categoryRepository.save(newCategory);
        return categoryMapper.toDto(newCategory);
    }

    public void deleteCategory(Integer id) {
        CompanyCategory companyCategory = getCategoryById(id);
        categoryRepository.delete(companyCategory);
    }
}
