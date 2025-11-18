package com.example.service.it_map;

import com.example.dto.agregator_dto.CompanyCategoryDto;
import com.example.model.it_map.CompanyCategory;
import com.example.repository.it_map.CompanyCategoryRepository;
import com.example.repository.it_map.CompanyRepository;
import com.example.service.common.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class CompanyCategoryTest {
    @MockitoSpyBean
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyCategoryRepository companyCategoryRepository;
    @Autowired
    private CompanyCategoryService companyCategoryService;
    @Autowired
    private FileStorageService fileStorageService;


    @BeforeEach
    void setUp() {
        companyRepository.deleteAll();
        companyCategoryRepository.deleteAll();
    }

    @Test
    void shouldSaveCategory() {
        String categoryName = "Telecom";
        CompanyCategoryDto dtoToSave = new CompanyCategoryDto(null, categoryName, null);
        CompanyCategoryDto dtoSaved = companyCategoryService.createCategory(dtoToSave);
        assertEquals(categoryName, dtoSaved.name());
    }

    @Test
    void shouldGetAllCategories() {
        String categoryTelecom = "Telecom";
        String categoryBigTech = "BigTech";
        CompanyCategoryDto dtoTelecom = new CompanyCategoryDto(null, categoryTelecom, null);
        CompanyCategoryDto dtoBigTech = new CompanyCategoryDto(null, categoryBigTech, null);
        companyCategoryService.createCategory(dtoTelecom);
        companyCategoryService.createCategory(dtoBigTech);
        List<CompanyCategoryDto> arrayFromDb = companyCategoryService.getAllCompaniesCategory();
        assertEquals(2, arrayFromDb.size());
    }

    @Test
    void shouldGetAllById() {
        String categoryTelecom = "Telecom";
        String categoryBigTech = "BigTech";
        CompanyCategoryDto dtoTelecom = new CompanyCategoryDto(null, categoryTelecom, null);
        CompanyCategoryDto dtoBigTech = new CompanyCategoryDto(null, categoryBigTech, null);
        CompanyCategoryDto dtoTelecomSaved = companyCategoryService.createCategory(dtoTelecom);
        CompanyCategoryDto dtoBigTechSaved = companyCategoryService.createCategory(dtoBigTech);
        Set<CompanyCategory> arrayFromDb = companyCategoryService.getAllCategoryByArray(List.of(dtoTelecomSaved.id(), dtoBigTechSaved.id()));
        assertEquals(2, arrayFromDb.size());
    }
}
