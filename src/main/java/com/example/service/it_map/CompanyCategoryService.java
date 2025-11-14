package com.example.service.it_map;

import com.example.dto.agregator_dto.CompanyCategoryDto;
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

    public Set<CompanyCategory> getOrCreateCategory(List<String> nameCategories) {
        List<CompanyCategory> categoryFromDB = categoryRepository.findByNameIn(nameCategories);
        Set<CompanyCategory> resultSet = new HashSet<>();
        if (categoryFromDB.isEmpty()) {
            nameCategories.forEach(category->{
                CompanyCategory newCategory = new CompanyCategory();
                newCategory.setName(category);
                resultSet.add(newCategory);
            });
        }
        else {
            List<String> tempList = categoryFromDB.stream().map(CompanyCategory::getName).toList();
            nameCategories.forEach(name->{
                if (!tempList.contains(name)){
                    CompanyCategory newCategory = new CompanyCategory();
                    newCategory.setName(name);
                    resultSet.add(newCategory);
                }
            });
        }
        List<CompanyCategory> savedAll = categoryRepository.saveAll(resultSet);
        return new HashSet<>(savedAll);
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
