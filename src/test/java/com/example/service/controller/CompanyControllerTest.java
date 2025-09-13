package com.example.service.controller;

import com.example.model.Company;
import com.example.model.CompanyCategory;
import com.example.repository.CompanyCategoryRepository;
import com.example.repository.CompanyRepository;
import com.example.repository.RecruiterRepository;
import com.example.service.company.CompanyService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyCategoryRepository companyCategoryRepository;
    @Autowired
    private CompanyService companyService;

    private final String endpointBase = "/v1/companies";

    @BeforeEach
    void setUp() {
        recruiterRepository.deleteAll();
        companyRepository.deleteAll();
        companyCategoryRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void shouldReturnCompaniesByCategories() throws Exception {
        // given
        CompanyCategory bigTech = new CompanyCategory();
        bigTech.setName("BigTech");
        companyCategoryRepository.save(bigTech);

        CompanyCategory finTech = new CompanyCategory();
        finTech.setName("FinTech");
        companyCategoryRepository.save(finTech);

        Company c1 = new Company();
        c1.setName("Google");
        c1.setCategory(bigTech);
        companyRepository.save(c1);

        Company c2 = new Company();
        c2.setName("Stripe");
        c2.setCategory(finTech);
        companyRepository.save(c2);

        Company c3 = new Company();
        c3.setName("OtherCo");
        // компания без категории или с другой категорией
        companyRepository.save(c3);

        // when & then
        mockMvc.perform(get(endpointBase + "/filter")
                        .param("categories", "BigTech", "FinTech")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].name").value(org.hamcrest.Matchers.containsInAnyOrder("Google", "Stripe")));
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListIfNoMatches() throws Exception {
        // given
        CompanyCategory other = new CompanyCategory();
        other.setName("E-com");
        companyCategoryRepository.save(other);

        Company c = new Company();
        c.setName("SomeCompany");
        c.setCategory(other);
        companyRepository.save(c);

        // when & then
        mockMvc.perform(get(endpointBase + "/filter")
                        .param("categories", "BigTech", "FinTech")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
