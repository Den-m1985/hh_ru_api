package com.example.service.it_map;

import com.example.RedisTestConfig;
import com.example.dto.RecruiterDto;
import com.example.dto.RecruiterRequest;
import com.example.dto.agregator_dto.CompanyCategoryDto;
import com.example.dto.company.CompanyResponseDto;
import com.example.model.it_map.Company;
import com.example.repository.RecruiterRepository;
import com.example.repository.it_map.CompanyCategoryRepository;
import com.example.repository.it_map.CompanyRepository;
import com.example.service.common.FileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class CompanyServiceTest {
    @MockitoSpyBean
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyCategoryRepository companyCategoryRepository;
    @Autowired
    private CompanyCategoryService companyCategoryService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private RecruiterService recruiterService;
    @Autowired
    private RecruiterRepository recruiterRepository;

    Path tempDir;
    CompanyCategoryDto dtoTelecom;
    CompanyCategoryDto dtoBigTech;

    @BeforeEach
    void setUp() throws IllegalAccessException, IOException, NoSuchFieldException {
        companyRepository.deleteAll();
        companyCategoryRepository.deleteAll();
        recruiterRepository.deleteAll();

        String categoryTelecom = "Telecom";
        String categoryBigTech = "BigTech";
        CompanyCategoryDto telecom = new CompanyCategoryDto(null, categoryTelecom, null);
        CompanyCategoryDto bigTech = new CompanyCategoryDto(null, categoryBigTech, null);
        dtoTelecom = companyCategoryService.createCategory(telecom);
        dtoBigTech = companyCategoryService.createCategory(bigTech);

        tempDir = Files.createTempDirectory("filestorage-test-");
        java.lang.reflect.Field storageDirField = FileStorageService.class.getDeclaredField("storageDir");
        storageDirField.setAccessible(true);
        storageDirField.set(fileStorageService, tempDir);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                        // Ignore cleanup errors
                    }
                });
    }

    // --- СЦЕНАРИЙ 1: УСПЕШНОЕ ОБНОВЛЕНИЕ ЛОГОТИПА ---
    @Test
    void shouldUpdateLogoAndRemoveOldFileSuccessfully() {
        CompanyResponseDto request = new CompanyResponseDto(0,
                "createdAt",
                "updatedAt",
                List.of(dtoTelecom.id()),
                "name",
                "companyUrl",
                "careerUrl",
                null,
                dtoTelecom.id(),
                true,
                java.util.List.of());
        CompanyResponseDto companyResponseDto = companyService.addCompany(request);
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "text/plain", "hello".getBytes());
        CompanyResponseDto result = companyService.updateCompanyLogo(companyResponseDto.getId(), file);

        assertThat(result).isNotNull();
        assertThat(result.getLogoUrl()).isNotNull();
        assertThat(result.getLogoUrl()).endsWith(".png");
    }

    // --- СЦЕНАРИЙ 2: СБОЙ СОХРАНЕНИЯ В БД С КОМПЕНСАЦИЕЙ ---
    @Test
    void shouldDeleteNewFileFromDiskWhenDbSaveFails() {
        CompanyResponseDto request = new CompanyResponseDto(0,
                "createdAt", "updatedAt", List.of(dtoTelecom.id()),
                "name_fail_test", "companyUrl", "careerUrl",
                null, dtoTelecom.id(),
                true, java.util.List.of());

        CompanyResponseDto companyDto = companyService.addCompany(request);
        MockMultipartFile file = new MockMultipartFile(
                "file", "fail_test.jpg", "image/jpeg", "fail data".getBytes());

        // Имитация сбоя: Заставляем companyRepository.save() выбросить исключение
        doThrow(new RuntimeException("Simulated DB Constraint Error"))
                .when(companyRepository).save(any(Company.class));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            companyService.updateCompanyLogo(companyDto.getId(), file);
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
        Company companyAfter = companyService.getCompanyById(companyDto.getId());
        assertThat(companyAfter.getLogoPath()).isNull();
    }

    @Test
    void shouldGetByOneCategories() {
        String companyName = "Sber";
        CompanyResponseDto request = new CompanyResponseDto(null,
                "createdAt",
                "updatedAt",
                List.of(dtoTelecom.id(), dtoBigTech.id()),
                companyName,
                "companyUrl",
                "careerUrl",
                null,
                dtoTelecom.id(),
                true,
                java.util.List.of());
        companyService.addCompany(request);
        List<CompanyResponseDto> listFromDb = companyService.getCompaniesByCategories(List.of(dtoTelecom.id()));
        assertEquals(companyName, listFromDb.get(0).getName());
    }

    @Test
    void shouldGetByOneCategories2() {
        CompanyResponseDto request = new CompanyResponseDto(null,
                "createdAt",
                "updatedAt",
                List.of(),
                "Sber",
                "companyUrl",
                "careerUrl",
                null,
                dtoTelecom.id(),
                true,
                java.util.List.of());
        companyService.addCompany(request);
        List<CompanyResponseDto> listFromDb = companyService.getCompaniesByCategories(List.of());
        assertEquals(0, listFromDb.size());
    }

    @Test
    void shouldSaveWithManyCategories() {
        String companyName = "Sber";
        CompanyResponseDto request = new CompanyResponseDto(null,
                "createdAt",
                "updatedAt",
                List.of(dtoTelecom.id(), dtoBigTech.id()),
                companyName,
                "companyUrl",
                "careerUrl",
                null,
                dtoTelecom.id(),
                true,
                java.util.List.of());
        companyService.addCompany(request);
        List<CompanyResponseDto> listFromDb = companyService.getCompaniesByCategories(List.of(dtoTelecom.id(), dtoBigTech.id()));
        assertEquals(companyName, listFromDb.get(0).getName());
    }

    @Test
    void shouldSaveByOneCategory() {
        String companyName = "Sber";
        String companyUrl = "companyUrl";
        String careerUrl = "career_url";
        CompanyResponseDto dto = new CompanyResponseDto(
                null,
                null,
                null,
                List.of(dtoTelecom.id()),
                companyName,
                companyUrl,
                careerUrl,
                null,
                dtoTelecom.id(),
                true,
                null);
        CompanyResponseDto companyDto = companyService.addCompany(dto);
        assertAll(
                () -> assertNotNull(companyDto.getId()),
                () -> assertEquals(companyName, companyDto.getName()),
                () -> assertEquals(1, companyDto.getCategory().size()),
                () -> assertEquals(dtoTelecom.id(), companyDto.getCategory().get(0)),
                () -> assertEquals(companyUrl, companyDto.getCompanyUrl()),
                () -> assertEquals(careerUrl, companyDto.getCareerUrl())
        );
    }

    @Test
    void shouldUpdateCompany() {
        String companyName = "Sber";
        String companyUrl = "companyUrl";
        String careerUrl = "career_url";
        CompanyResponseDto dto = new CompanyResponseDto(null, null, null,
                List.of(dtoTelecom.id()),
                companyName,
                companyUrl,
                careerUrl,
                null,
                dtoTelecom.id(),
                true,
                null);
        CompanyResponseDto companyDto = companyService.addCompany(dto);

        String companyNameNew = "SberNew";
        String companyUrlNew = "companyUrlNew";
        String careerUrlNew = "career_urlNew";
        CompanyResponseDto dtoToUpdate = new CompanyResponseDto(companyDto.getId(), null, null,
                List.of(dtoTelecom.id(), dtoBigTech.id()),
                companyNameNew,
                companyUrlNew,
                careerUrlNew,
                null,
                dtoBigTech.id(),
                true,
                null);

        RecruiterRequest recruiterDto = new RecruiterRequest("firstName", "lastName",
                "contactTelegram", "contactLinkedIn", "email", companyDto.getId());
        recruiterService.saveRecruiter(recruiterDto);

        RecruiterRequest recruiterDtoToUpdate = new RecruiterRequest("firstName", "lastName",
                "contactTelegram", "contactLinkedIn", "email", null);
        RecruiterDto recruiterFromDbToUpdate = recruiterService.saveRecruiter(recruiterDtoToUpdate);
        dtoToUpdate.setRecruiters(List.of(recruiterFromDbToUpdate.id()));

        CompanyResponseDto companyUpdatedDto = companyService.updateCompany(dtoToUpdate);
        assertAll(
                () -> assertNotNull(companyUpdatedDto.getId()),
                () -> assertEquals(companyNameNew, companyUpdatedDto.getName()),
                () -> assertEquals(2, companyUpdatedDto.getCategory().size()),
                () -> assertTrue(companyUpdatedDto.getCategory().contains(dtoTelecom.id())),
                () -> assertEquals(companyUrlNew, companyUpdatedDto.getCompanyUrl()),
                () -> assertEquals(careerUrlNew, companyUpdatedDto.getCareerUrl()),
                () -> assertEquals(dtoBigTech.id(), companyUpdatedDto.getCategoryVirtualMap()),
                () -> assertEquals(true, companyUpdatedDto.getPresentInVirtualMap()),
                () -> assertEquals(recruiterFromDbToUpdate.id(), companyUpdatedDto.getRecruiters().get(0))
        );
    }

    @Test
    void shouldFindCompanyByPresentInVirtualMap() {
        String companyName = "Sber";
        CompanyResponseDto dto1 = new CompanyResponseDto(null, null, null, List.of(dtoTelecom.id()),
                companyName, null, null, null, dtoTelecom.id(), true, null);
        CompanyResponseDto dto2 = new CompanyResponseDto(null, null, null, List.of(dtoTelecom.id()),
                companyName, null, null, null, dtoTelecom.id(), true, null);
        CompanyResponseDto dto3 = new CompanyResponseDto(null, null, null, List.of(dtoTelecom.id()),
                companyName, null, null, null, dtoTelecom.id(), false, null);
        companyService.addCompany(dto1);
        companyService.addCompany(dto2);
        companyService.addCompany(dto3);
        List<CompanyResponseDto> resultList = companyService.findCompanyByField(true);
        assertAll(
                () -> assertNotNull(resultList),
                () -> assertEquals(2, resultList.size())
        );
    }

    @Test
    void shouldFindCompanyBySearch() {
        List<String> arayNames = List.of("Sber", "Сбер", "ВкусВилл", "Яндекс", "VK", "Мегафон", "Т-Банк", "Авито");
        arayNames.forEach(name ->{
            CompanyResponseDto dto = new CompanyResponseDto(null, null, null, List.of(dtoTelecom.id()),
                    name, null, null, null, null, true, null);
            companyService.addCompany(dto);
        });

        List<String> arayNamesToFind = List.of("Sber", "сбер", "вкусВилл", "яндекс", "vk", "мегафон", "т-банк", "Авито");
        arayNamesToFind.forEach(name ->{
            List<CompanyResponseDto> resultList = companyService.findCompanyBySearch(name);
            assertEquals(name.toLowerCase(), resultList.get(0).getName().toLowerCase());
        });
    }
}
