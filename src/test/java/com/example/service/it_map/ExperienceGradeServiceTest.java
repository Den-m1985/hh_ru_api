package com.example.service.it_map;

import com.example.model.agregator.ExperienceGrade;
import com.example.repository.it_map.ExperienceGradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ExperienceGradeServiceTest {
    @Autowired
    private ExperienceGradeRepository experienceGradeRepository;
    @Autowired
    private ExperienceGradeService experienceGradeService;

    @BeforeEach
    void setUp() {
        experienceGradeRepository.deleteAll();
    }


    @Test
    void shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> experienceGradeService.getOrCreateGrade(null, null, ""));
    }

    @Test
    void shouldGetById() {
        ExperienceGrade newGrade = new ExperienceGrade();
        newGrade.setName("Name");
        newGrade.setDescription("Description");
        experienceGradeRepository.save(newGrade);
        ExperienceGrade gradeFromDB = experienceGradeService.getOrCreateGrade(newGrade.getId(), "", "");
        assertEquals(newGrade.getId(), gradeFromDB.getId());
    }

    @Test
    void shouldGetByName() {
        ExperienceGrade newGrade = new ExperienceGrade();
        String name = "Name";
        newGrade.setName(name);
        newGrade.setDescription("Description");
        experienceGradeRepository.save(newGrade);
        ExperienceGrade gradeFromDB = experienceGradeService.getOrCreateGrade(null, name, "");
        assertEquals(newGrade.getId(), gradeFromDB.getId());
    }

    @Test
    void shouldGetByNameAndId() {
        ExperienceGrade newGrade = new ExperienceGrade();
        String name = "Name";
        newGrade.setName(name);
        newGrade.setDescription("Description");
        experienceGradeRepository.save(newGrade);
        ExperienceGrade gradeFromDB = experienceGradeService.getOrCreateGrade(newGrade.getId(), name, "");
        assertEquals(newGrade.getId(), gradeFromDB.getId());
    }
}
