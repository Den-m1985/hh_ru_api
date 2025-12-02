package com.example.controller.it_map;

import com.example.RedisTestConfig;
import com.example.dto.agregator_dto.CompanyCategoryDto;
import com.example.dto.company.CompanyResponseDto;
import com.example.model.it_map.Company;
import com.example.model.it_map.CompanyCategory;
import com.example.repository.it_map.CompanyCategoryRepository;
import com.example.repository.it_map.CompanyRepository;
import com.example.service.common.FileStorageService;
import com.example.service.it_map.CompanyCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(RedisTestConfig.class)
@ActiveProfiles("test")
class CompanyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyCategoryRepository companyCategoryRepository;
    @Autowired
    private CompanyCategoryService companyCategoryService;
    @Autowired
    private FileStorageService fileStorageService;

    Path tempDir;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String endpointBase = "/v1/it_map/companies";

    @BeforeEach
    void setUp() throws IOException, IllegalAccessException, NoSuchFieldException {
        companyRepository.deleteAll();
        companyCategoryRepository.deleteAll();
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

    @Test
    @WithMockUser
    void shouldReturnCompaniesByCategories() throws Exception {
        String categoryTelecom = "Telecom";
        String categoryBigTech = "BigTech";
        CompanyCategoryDto dtoTelecom = new CompanyCategoryDto(null, categoryTelecom, null);
        CompanyCategoryDto dtoBigTech = new CompanyCategoryDto(null, categoryBigTech, null);
        CompanyCategoryDto dtoTelecomSaved = companyCategoryService.createCategory(dtoTelecom);
        CompanyCategoryDto dtoBigTechSaved = companyCategoryService.createCategory(dtoBigTech);
        CompanyCategory companyCategoryTelecom = companyCategoryService.getCategoryById(dtoTelecomSaved.id());
        CompanyCategory companyCategoryBigTech = companyCategoryService.getCategoryById(dtoBigTechSaved.id());

        Company c1 = new Company();
        c1.setName("Google");
        c1.setCategories(Set.of(companyCategoryTelecom));
        companyRepository.save(c1);

        Company c2 = new Company();
        c2.setName("Stripe");
        c2.setCategories(Set.of(companyCategoryBigTech));
        companyRepository.save(c2);

        Company c3 = new Company();
        c3.setName("OtherCo");
        companyRepository.save(c3);

        mockMvc.perform(get(endpointBase + "/filter")
                        .param("categories", String.valueOf(dtoTelecomSaved.id()), String.valueOf(dtoBigTechSaved.id()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].name").value(org.hamcrest.Matchers.containsInAnyOrder("Google", "Stripe")));
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListIfNoMatches() throws Exception {
        String categoryName = "Telecom";
        CompanyCategoryDto dtoToSave = new CompanyCategoryDto(null, categoryName, null);
        CompanyCategoryDto companyCategoryDto = companyCategoryService.createCategory(dtoToSave);
        CompanyCategory companyCategory = companyCategoryService.getCategoryById(companyCategoryDto.id());
        Company company = new Company();
        company.setName("SomeCompany");
        company.setCategories(Set.of(companyCategory));
        companyRepository.save(company);

        mockMvc.perform(get(endpointBase + "/filter")
                        .param("categories", String.valueOf(companyCategoryDto.id() + 1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void shouldUploadCompanyLogo() throws Exception {
        CompanyCategory category = new CompanyCategory();
        category.setName("FinTech");
        companyCategoryRepository.save(category);
        Company company = new Company();
        company.setName("Stripe");
        company.setCategories(Set.of(category));
        company = companyRepository.save(company);

        MockMultipartFile mockFile = new MockMultipartFile(
                "File",                        // <-- название параметра должно совпадать с @RequestPart("File")
                "logo.png",                    // оригинальное имя файла
                "image/png",                   // MIME type
                "fake-image-content".getBytes() // содержимое файла (просто строка вместо картинки)
        );

        mockMvc.perform(multipart(endpointBase + "/" + company.getId() + "/logo")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Stripe"));

        Company updated = companyRepository.findById(company.getId()).orElseThrow();
        assertThat(updated.getLogoPath()).isNotBlank();
    }

    @Test
    @WithMockUser
    void shouldUploadLogoAndReturnPath() throws Exception {
        CompanyCategory category = new CompanyCategory();
        category.setName("BigTech");
        companyCategoryRepository.save(category);
        Company company = new Company();
        company.setName("AcmeCorp");
        company.setCategories(Set.of(category));
        company = companyRepository.save(company);
        final Integer companyId = company.getId();

        String logoContent = "Mock image data";
        MockMultipartFile mockFile = new MockMultipartFile("File",
                "logo.png", "image/png", logoContent.getBytes());

        mockMvc.perform(multipart(endpointBase + "/{companyId}/logo", companyId)
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logo_url").exists());

        Company updatedCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new AssertionError("Company not found after update."));
        assertThat(updatedCompany.getLogoPath()).isNotNull();
    }


    @Test
    @WithMockUser
    void shouldUploadLogoToCompany() throws Exception {
        CompanyCategory category = new CompanyCategory();
        category.setName("IT");
        companyCategoryRepository.save(category);

        Company company = new Company();
        company.setName("Test Company");
        company.setCategories(Set.of(category));
        company = companyRepository.save(company);

        MockMultipartFile logoFile = new MockMultipartFile(
                "File", "logo.png", "image/png", "fake image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(endpointBase + "/" + company.getId() + "/logo")
                        .file(logoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(company.getId()))
                .andExpect(jsonPath("$.logo_url").isNotEmpty());

        Company updatedCompany = companyRepository.findById(company.getId()).orElseThrow();
        assertNotNull(updatedCompany.getLogoPath());
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenUploadLogoToNonExistentCompany() throws Exception {
        MockMultipartFile logoFile = new MockMultipartFile(
                "File",
                "logo.png",
                "image/png",
                "fake image content".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart(endpointBase + "/999/logo")
                        .file(logoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldGenerateUniqueFilenamesForSameCompany() throws Exception {
        CompanyCategory category = new CompanyCategory();
        category.setName("IT");
        companyCategoryRepository.save(category);

        Company company = new Company();
        company.setName("TestCompany");
        company.setCategories(Set.of(category));
        company = companyRepository.save(company);

        MockMultipartFile logoFile1 = new MockMultipartFile(
                "File", "logo.png", "image/png", "content1".getBytes());

        MockMultipartFile logoFile2 = new MockMultipartFile(
                "File", "logo.png", "image/png", "content2".getBytes());


        mockMvc.perform(MockMvcRequestBuilders.multipart(endpointBase + "/" + company.getId() + "/logo")
                        .file(logoFile1))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.multipart(endpointBase + "/" + company.getId() + "/logo")
                        .file(logoFile2))
                .andExpect(status().isOk());

        Company updatedCompany = companyRepository.findById(company.getId()).orElseThrow();
        assertNotNull(updatedCompany.getLogoPath());
    }

    @Test
    @WithMockUser
    void shouldHandleEmptyFileUpload() throws Exception {
        CompanyCategory category = new CompanyCategory();
        category.setName("IT");
        companyCategoryRepository.save(category);

        Company company = new Company();
        company.setName("Test Company");
        company.setCategories(Set.of(category));
        company = companyRepository.save(company);

        MockMultipartFile emptyFile = new MockMultipartFile(
                "File", "empty.png", "image/png", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart(endpointBase + "/" + company.getId() + "/logo")
                        .file(emptyFile))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser
    void shouldAddCompanyWithLogoSuccessfully() throws Exception {
        String companyName = "NewGenCorp";
        CompanyCategory bigTech = new CompanyCategory();
        bigTech.setName("BigTech");
        companyCategoryRepository.save(bigTech);

        CompanyResponseDto inputDto = new CompanyResponseDto(
                null, null, null, List.of(0), companyName,
                "http://newgencorp.com", "http://newgencorp.com/career", null, null,
                null, List.of());
        String companyDataJson = objectMapper.writeValueAsString(inputDto);

        String logoContent = "Mock image data";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logoFile",
                "logo.png",
                "image/png",
                logoContent.getBytes()
        );

        MockMultipartFile companyDataPart = new MockMultipartFile(
                "companyData", null, MediaType.APPLICATION_JSON, companyDataJson.getBytes());

        mockMvc.perform(multipart(endpointBase + "/add/with-logo")
                        .file(logoFile)
                        .file(companyDataPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(companyName))
                .andExpect(jsonPath("$.logo_url").exists());

        Company savedCompany = companyRepository.findCompanyByName(companyName)
                .orElseThrow(() -> new AssertionError("Company not saved in DB."));
        assertThat(savedCompany.getLogoPath()).isNotNull();
    }

}
