package com.example.service.it_map;

import com.example.dto.agregator_dto.CompetencyMatrixRequest;
import com.example.dto.it_map.CompetencyAreasRequest;
import com.example.dto.it_map.CompetencyRequest;
import com.example.model.agregator.CompetencyMatrix;
import com.example.model.agregator.ExperienceGrade;
import com.example.repository.it_map.CompetencyAreasRepository;
import com.example.repository.it_map.CompetencyMatrixRepository;
import com.example.repository.it_map.ExperienceGradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class CompetencyMatrixServiceTest {
    @Autowired
    private CompetencyMatrixRepository competencyMatrixRepository;
    @Autowired
    private CompetencyAreasRepository competencyAreasRepository;
    @Autowired
    private ExperienceGradeRepository experienceGradeRepository;
    @Autowired
    private ExperienceGradeService experienceGradeService;
    @Autowired
    private CompetencyMatrixService competencyMatrixService;

    @BeforeEach
    void setUp() {
        competencyMatrixRepository.deleteAll();
        competencyAreasRepository.deleteAll();
        experienceGradeRepository.deleteAll();
    }

    @Test
    void shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> competencyMatrixService.addCompetencyMatrix(null));
    }

    @Test
    @Transactional
    void shouldGetById() {
        ExperienceGrade experienceGrade = experienceGradeService.getOrCreateGrade(null, "Junior", null);

        CompetencyRequest competencyRequest = new CompetencyRequest(
                null,
                "ООП: наследование, полиморфизм, инкапсуляция",
                experienceGrade.getId(),
                experienceGrade.getName(),
                List.of("extends", "методология")
        );
        CompetencyAreasRequest competencyAreasRequest = new CompetencyAreasRequest(
                null,
                "Core Java & ООП",
                List.of(competencyRequest)
        );
        CompetencyMatrixRequest request = new CompetencyMatrixRequest(
                null,
                "Java",
                List.of(competencyAreasRequest)
        );
        CompetencyMatrix competencyMatrix = competencyMatrixService.addCompetencyMatrix(request);

        assertNotNull(competencyMatrix);
        assertEquals(request.specialization(), competencyMatrix.getSpecialization());
        assertEquals(
                competencyRequest.experienceGradeName(),
                competencyMatrix.getCompetencyAreas().get(0).getCompetencies().get(0).getExperienceGrade().getName()
        );
        assertEquals(
                competencyAreasRequest.area(),
                competencyMatrix.getCompetencyAreas().get(0).getArea()
        );
    }
}
