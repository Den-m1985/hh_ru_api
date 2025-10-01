package com.example.controller;

import com.example.dto.agregator_dto.CompaniesProfileRequest;
import com.example.dto.agregator_dto.CompaniesProfileResponse;
import com.example.dto.company.CompanyResponseDto;
import com.example.model.Company;
import com.example.model.CompanyCategory;
import com.example.model.agregator.CompetencyMatrix;
import com.example.model.agregator.ExperienceGrade;
import com.example.repository.CompanyCategoryRepository;
import com.example.repository.CompanyRepository;
import com.example.repository.agregator.CompetencyMatrixRepository;
import com.example.repository.agregator.ExperienceGradeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanyReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyCategoryRepository companyCategoryRepository;
    @Autowired
    private ExperienceGradeRepository experienceGradeRepository;
    @Autowired
    private CompetencyMatrixRepository competencyMatrixRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String endpointBase = "/v1/interviews";

    @BeforeEach
    void setUp() {
        companyRepository.deleteAll();
        companyCategoryRepository.deleteAll();
        experienceGradeRepository.deleteAll();
        competencyMatrixRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void shouldReturnCompaniesCard() throws Exception {
        ExperienceGrade experienceGrade = new ExperienceGrade();
        experienceGrade.setName("Junior");
        experienceGrade.setDescription("first level");
        experienceGrade = experienceGradeRepository.save(experienceGrade);

        CompetencyMatrix competencyMatrix = new CompetencyMatrix();
        competencyMatrix.setSpecialization("Java");
        competencyMatrix.setCompetencies("link to competencies");
        competencyMatrix.setTechnicalQuestions("link to technical questions");
        competencyMatrix = competencyMatrixRepository.save(competencyMatrix);

        CompanyCategory bigTech = new CompanyCategory();
        bigTech.setName("BigTech");
        companyCategoryRepository.save(bigTech);

        Company company = new Company();
        company.setName("Google");
        company.setCategory(bigTech);
        company = companyRepository.save(company);

        CompanyResponseDto companyResponseDto = new CompanyResponseDto(
                company.getId(),
                company.getCreatedAt() != null ? company.getCreatedAt().format(formatter) : null,
                company.getUpdatedAt() != null ? company.getCreatedAt().format(formatter) : null,
                bigTech.getName(), company.getName(), company.getCompanyUrl(), company.getCareerUrl(), company.getLogoPath(), List.of()
        );

        CompaniesProfileRequest companiesProfileRequest = new CompaniesProfileRequest(
                competencyMatrix.getSpecialization(),
                experienceGrade.getId(),
                List.of(bigTech.getName())
        );

        CompaniesProfileResponse companiesProfileResponse = new CompaniesProfileResponse(
                experienceGrade.getName(), experienceGrade.getDescription(),
                competencyMatrix.getSpecialization(), competencyMatrix.getCompetencies(), competencyMatrix.getTechnicalQuestions(),
                List.of(companyResponseDto)
        );

        String expectedJson = objectMapper.writeValueAsString(companiesProfileResponse);

        mockMvc.perform(post(endpointBase + "/filter")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companiesProfileRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
