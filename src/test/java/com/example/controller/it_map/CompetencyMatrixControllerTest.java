package com.example.controller.it_map;

import com.example.dto.it_map.CompetencyAreasResponse;
import com.example.dto.it_map.CompetencyMatrixFilterRequest;
import com.example.mapper.CompetencyMatrixMapper;
import com.example.model.agregator.CompetencyMatrix;
import com.example.model.agregator.ExperienceGrade;
import com.example.model.it_map.Competency;
import com.example.model.it_map.CompetencyAreas;
import com.example.repository.it_map.CompetencyAreasRepository;
import com.example.repository.it_map.CompetencyMatrixRepository;
import com.example.repository.it_map.CompetencyRepository;
import com.example.repository.it_map.ExperienceGradeRepository;
import com.example.service.it_map.CompetencyMatrixService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompetencyMatrixControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ExperienceGradeRepository experienceGradeRepository;
    @Autowired
    private CompetencyMatrixRepository competencyMatrixRepository;
    @Autowired
    private CompetencyAreasRepository competencyAreasRepository;
    @Autowired
    private CompetencyRepository competencyRepository;
    @Autowired
    private CompetencyMatrixService competencyMatrixService;
    @Autowired
    private CompetencyMatrixMapper competencyMatrixMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String endpointBase = "/v1/it_map/competency";
    private CompetencyMatrix matrix;


    @BeforeEach
    void setUp() {
        experienceGradeRepository.deleteAll();
        competencyMatrixRepository.deleteAll();
        competencyAreasRepository.deleteAll();
        competencyRepository.deleteAll();
        init();
    }

    @Test
    @WithMockUser
    void shouldReturnException() throws Exception {
        CompetencyMatrixFilterRequest request = new CompetencyMatrixFilterRequest(null, "Junior");

        mockMvc.perform(post(endpointBase + "/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldReturnSuccessResult() throws Exception {
        CompetencyMatrixFilterRequest request = new CompetencyMatrixFilterRequest("Frontend", "Junior");

        mockMvc.perform(post(endpointBase + "/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void shouldReturnEqualsResult() throws Exception {
        CompetencyMatrixFilterRequest request = new CompetencyMatrixFilterRequest("Frontend", "Junior");
        String response = initResponse();
        mockMvc.perform(post(endpointBase + "/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyResult() throws Exception {
        CompetencyMatrixFilterRequest request = new CompetencyMatrixFilterRequest("Fron", "Junior");

        mockMvc.perform(post(endpointBase + "/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    private void init() {
        matrix = new CompetencyMatrix();
        matrix.setSpecialization("Frontend");
        matrix.setCompetencyAreas(new java.util.ArrayList<>());

        CompetencyAreas competencyAreas = new CompetencyAreas();
        competencyAreas.setCompetencyMatrix(matrix);
        competencyAreas.setArea("HTML & CSS");
        competencyAreas.setCompetencies(new java.util.ArrayList<>());
        matrix.getCompetencyAreas().add(competencyAreas);

        ExperienceGrade experienceGradeJunior = new ExperienceGrade();
        experienceGradeJunior.setName("Junior");

        Competency competencySemantic = new Competency();
        competencySemantic.setCompetencyAreas(competencyAreas);
        competencySemantic.setName("Семантика");
        competencySemantic.setExperienceGrade(experienceGradeJunior);
        competencySemantic.setAttribute(List.of("<header>", "<section>"));
        experienceGradeJunior.setCompetency(competencySemantic);
        competencyAreas.getCompetencies().add(competencySemantic);

        Competency competencyFlexbox = new Competency();
        competencyFlexbox.setCompetencyAreas(competencyAreas);
        competencyFlexbox.setName("Flexbox & Grid");
        competencyFlexbox.setExperienceGrade(experienceGradeJunior);
        competencyFlexbox.setAttribute(List.of("Basic constraction"));
        experienceGradeJunior.setCompetency(competencyFlexbox);
        competencyAreas.getCompetencies().add(competencyFlexbox);

        competencyMatrixRepository.save(matrix);
        competencyAreasRepository.save(competencyAreas);
        competencyRepository.saveAll(List.of(competencySemantic, competencyFlexbox));
        experienceGradeRepository.save(experienceGradeJunior);
    }

    private String initResponse() throws JsonProcessingException {
        List<CompetencyAreasResponse> response;
        response = competencyMatrixMapper.mapCompetencyAreasToDto(matrix.getCompetencyAreas());
        return objectMapper.writeValueAsString(response);
    }
}
