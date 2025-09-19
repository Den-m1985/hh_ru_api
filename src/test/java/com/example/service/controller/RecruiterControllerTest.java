package com.example.service.controller;

import com.example.dto.RecruiterRequest;
import com.example.dto.company.CompanyResponseDto;
import com.example.model.Recruiter;
import com.example.repository.RecruiterRepository;
import com.example.service.aggregator.RecruiterService;
import com.example.service.company.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecruiterControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private RecruiterService recruiterService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String endpointBase = "/v1/recruiters/";

    @BeforeEach
    void setUp() {
        recruiterRepository.deleteAll();
    }

    @Test
    void shouldReturnForbidden() throws Exception {
        mockMvc.perform(get(endpointBase + 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void shouldReturnRecruiter() throws Exception {
        recruiterService.saveRecruiter(new RecruiterRequest(
                "firstName", "lastName", null, null, null, null));
        List<Recruiter> a = recruiterRepository.findAll();
        Recruiter recruiter = a.get(0);
        mockMvc.perform(get(endpointBase + recruiter.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recruiter.getId()))
                .andExpect(jsonPath("$.first_name").value(recruiter.getFirstName()));
    }

    @Test
    @WithMockUser
    void shouldReturnAllRecruiters() throws Exception {
        Recruiter recruiter1 = new Recruiter();
        Recruiter recruiter2 = new Recruiter();
        recruiterRepository.saveAll(List.of(recruiter1, recruiter2));
        mockMvc.perform(get(endpointBase + "all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    @WithMockUser
    void shouldReturnAllRecruitersByCompany() throws Exception {
        Recruiter recruiter1 = new Recruiter();
        Recruiter recruiter2 = new Recruiter();
        List<Recruiter> recruiters = recruiterRepository.saveAll(List.of(recruiter1, recruiter2));
        String companyName = "company";
        CompanyResponseDto companyResponseDto = new CompanyResponseDto(
                null,
                null,
                null,
                "IT",
                companyName,
                null,
                null,
                List.of(recruiters.get(0).getId(), recruiters.get(1).getId()));
        CompanyResponseDto company = companyService.addCompany(companyResponseDto);

        mockMvc.perform(get(endpointBase + "all_by_company/" + company.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void shouldAddRecruiter() throws Exception {
        RecruiterRequest recruiter = new RecruiterRequest(
                "first name", null, null, null, null, null
        );

        mockMvc.perform(post(endpointBase + "add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recruiter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("first name"));
    }
}
