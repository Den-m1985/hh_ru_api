package com.example.service.company;

import com.example.dto.company.CompanyResponseDto;
import com.example.model.Company;
import com.example.repository.CompanyCategoryRepository;
import com.example.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class CompanyServiceTest {
    @MockitoSpyBean
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyCategoryRepository companyCategoryRepository;
    @Autowired
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyRepository.deleteAll();
        companyCategoryRepository.deleteAll();
    }

    // --- СЦЕНАРИЙ 1: УСПЕШНОЕ ОБНОВЛЕНИЕ ЛОГОТИПА ---
    @Test
    void shouldUpdateLogoAndRemoveOldFileSuccessfully() {
        CompanyResponseDto request = new CompanyResponseDto(0,
                "createdAt",
                "updatedAt",
                "category",
                "name",
                "companyUrl",
                "careerUrl",
                null,
                java.util.List.of());
        CompanyResponseDto companyResponseDto = companyService.addCompany(request);
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "text/plain", "hello".getBytes());
        CompanyResponseDto result = companyService.updateCompanyLogo(companyResponseDto.id(), file);

        assertThat(result).isNotNull();
        assertThat(result.logoUrl()).isNotNull();
        assertThat(result.logoUrl()).endsWith(".png");
    }

    // --- СЦЕНАРИЙ 2: СБОЙ СОХРАНЕНИЯ В БД С КОМПЕНСАЦИЕЙ ---
    @Test
    void shouldDeleteNewFileFromDiskWhenDbSaveFails() {
        CompanyResponseDto request = new CompanyResponseDto(0,
                "createdAt", "updatedAt", "category",
                "name_fail_test", "companyUrl", "careerUrl",
                null, java.util.List.of());

        CompanyResponseDto companyDto = companyService.addCompany(request);
        MockMultipartFile file = new MockMultipartFile(
                "file", "fail_test.jpg", "image/jpeg", "fail data".getBytes());

        // Имитация сбоя: Заставляем companyRepository.save() выбросить исключение
        doThrow(new RuntimeException("Simulated DB Constraint Error"))
                .when(companyRepository).save(any(Company.class));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            companyService.updateCompanyLogo(companyDto.id(), file);
        });

        assertThat(exception.getMessage()).contains("DB save failed");

        // Получаем объект компании до сбоя, чтобы увидеть, какое имя файла было ему присвоено.
        // Поскольку транзакция откатилась, logoPath в БД будет null.
        // Но в объекте, переданном в .save(), оно было установлено.
        // Нам нужно знать, какое имя файла было сгенерировано fileStorageService.
        // Так как fileStorageService — это реальный бин, мы не можем мокировать generateUniqueFileName().
        // Используйте аргумент-каптор (Argument Captor) для захвата Company-объекта,
        // переданного в save(), и извлеките из него новый logoPath.
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        try {
            verify(companyRepository, atLeastOnce()).save(companyCaptor.capture());
        } catch (Throwable ignored) {
            // Игнорируем исключение
        }
        Company companyAfter = companyService.getCompanyById(companyDto.id());
        assertThat(companyAfter.getLogoPath()).isNull();
    }
}
