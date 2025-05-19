package com.example.service;

import com.example.dto.vacancy_dto.Experience;
import com.example.dto.vacancy_dto.ProfessionalRoles;
import com.example.dto.vacancy_dto.Snippet;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.dto.vacancy_dto.WorkFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VacancyFilterTest {
    private NegotiationsAll negotiationsAll;
    private VacancyFilter vacancyFilter;

    @BeforeEach
    void setUp() {
        negotiationsAll = mock(NegotiationsAll.class);
        vacancyFilter = new VacancyFilter(negotiationsAll);
    }

    @Test
    void shouldFilterOutNegotiatedArchivedAndKeywordedVacancies() {
        VacancyItem negotiated = createVacancy("Java Developer", false, false);
        VacancyItem archived = createVacancy("Kotlin Developer", false, true);
        VacancyItem withTest = createVacancy("Backend Developer", true, false);
        VacancyItem keyworded = createVacancy("Senior Python Engineer", false, false);
        VacancyItem valid = createVacancy("Kotlin разработчик", false, false);

        when(negotiationsAll.getNegotiationList()).thenReturn(List.of("1"));

        Set<VacancyItem> allVacancies = Set.of(negotiated, archived, withTest, keyworded, valid);
        List<VacancyItem> result = vacancyFilter.filterVacancies(allVacancies);

        assertEquals(2, result.size());
    }


    public VacancyItem createVacancy(String name, boolean hasTest, boolean archived) {
        return new VacancyItem(
                false,
                false,
                "https://hh.ru/vacancy/120348534",
                "https://hh.ru/applicant/vacancy_response?vacancyId=120348534",
                archived,
                null,
                null, // Contact
                "2025-05-14T18:14:21+0300",
                null, // Department
                null,
                hasTest,
                "randomDigit",
                false,
                name,
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
    }

}
