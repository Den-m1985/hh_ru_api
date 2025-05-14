package com.example.service;


import com.example.dto.vacancy_dto.ApiListResponse;
import com.example.dto.vacancy_dto.Experience;
import com.example.dto.vacancy_dto.ProfessionalRoles;
import com.example.dto.vacancy_dto.Snippet;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.dto.vacancy_dto.WorkFormat;
import com.example.util.ApplicationProperties;
import com.example.util.HeadHunterProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

class AllVacanciesTest {
    @Mock
    private VacancyClient vacancyClient;
    @Mock
    private ApplicationProperties applicationProperties;
    @Mock
    private HeadHunterProperties headHunterProperties;
    @Mock
    private PaginationCalculator paginationCalculator;

    @InjectMocks
    private AllVacancies allVacancies;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(applicationProperties.getPageIntervalMin()).thenReturn(0.1f);
        when(applicationProperties.getPageIntervalMax()).thenReturn(0.1f);
    }

    @Test
    void testGetAllVacancies() {
        String resumeId = "dummy_resume_id";
        when(headHunterProperties.getCountVacancies()).thenReturn(3);
        when(headHunterProperties.getSearchBySimilarVacancies()).thenReturn(false);

        Map<Integer, Integer> pageMap = new LinkedHashMap<>();
        pageMap.put(0, 2);
        pageMap.put(1, 1);

        when(paginationCalculator.calculatePages(3, 100)).thenReturn(pageMap);

        List<VacancyItem> data = initializeVacancies(3);

        when(vacancyClient.getSearchVacancies(0, 2))
                .thenReturn(new ApiListResponse<>(List.of(data.get(0), data.get(1)), 3, 0, 2, 2, ""));
        when(vacancyClient.getSearchVacancies(1, 1))
                .thenReturn(new ApiListResponse<>(List.of(data.get(2)), 3, 1, 2, 1, ""));

        Set<VacancyItem> result = allVacancies.getAllVacancies(resumeId);

        assertThat(result).hasSize(3);
    }

    public List<VacancyItem> initializeVacancies(int count) {
        List<VacancyItem> vacancies = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int randomDigit = random.nextInt(10000, Integer.MAX_VALUE);
            VacancyItem vacancyItem = new VacancyItem(
                    false,
                    false,
                    "https://hh.ru/vacancy/120348534",
                    "https://hh.ru/applicant/vacancy_response?vacancyId=120348534",
                    false,
                    null,
                    null, // Contact
                    "2025-05-14T18:14:21+0300",
                    null, // Department
                    null,
                    false,
                    String.valueOf(randomDigit),
                    false,
                    "Senior QA Automation Engineer (Java)",
                    false,
                    Collections.singletonList(new ProfessionalRoles("124", "Тестировщик")),
                    "2025-05-14T18:14:21+0300",
                    Collections.emptyList(),
                    false,
                    null,
                    null,
                    false,
                    null,
                    "https://api.hh.ru/vacancies/120348534?host=hh.ru",
                    Arrays.asList(new WorkFormat("ON_SITE", "На месте работодателя"), new WorkFormat("REMOTE", "Удалённо"), new WorkFormat("HYBRID", "Гибрид")),
                    null,
                    new Experience("between3And6", "От 3 до 6 лет"),
                    new Snippet("Strong emphasis on Java-based technologies (e.g., Selenium with Java, JUnit, TestNG).", "Creating and maintaining test frameworks...")
            );
            vacancies.add(vacancyItem);
        }
        return vacancies;
    }

}
