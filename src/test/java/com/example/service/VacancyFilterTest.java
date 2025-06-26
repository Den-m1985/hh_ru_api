package com.example.service;

import com.example.dto.FilterResult;
import com.example.dto.vacancy_dto.Experience;
import com.example.dto.vacancy_dto.ProfessionalRoles;
import com.example.dto.vacancy_dto.Snippet;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.dto.vacancy_dto.WorkFormat;
import com.example.enums.VacancyRelation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VacancyFilterTest {
    private VacancyFilter vacancyFilter;


    @BeforeEach
    void setUp() {
        vacancyFilter = new VacancyFilter();
    }

    @Test
    void shouldFilterOutNegotiatedArchivedAndKeywordedVacancies() {
        List<String> keywordsToExclude = List.of("Senior", "lead", "TeamLead", "Android", "QA", "Python",
                "Typescript", "Java Script", "JavaScript", "Go", "DevOps", "Oracle", "Node.js", "ReactJS", "Менеджер",
                "Лектор", "Ведущий", "Старший", "Тестировщик", "Fullstack", "фулстек", "Автотестировщик", "Главный",
                "Руководитель");

        List<VacancyRelation> relations = List.of(VacancyRelation.GOT_RESPONSE);
        VacancyItem vacancyToApply = createVacancy("Java Developer", false, false, Collections.emptyList());
        VacancyItem negotiated = createVacancy("Java Developer", false, false, relations);
        VacancyItem archived = createVacancy("Kotlin Developer", false, true, Collections.emptyList());
        VacancyItem withTest = createVacancy("Backend Developer", true, false, Collections.emptyList());
        VacancyItem keyworded = createVacancy("Senior Python Engineer", false, false, Collections.emptyList());
        VacancyItem valid = createVacancy("Kotlin разработчик", false, false, relations);

        Set<VacancyItem> allVacancies = Set.of(vacancyToApply, negotiated, archived, withTest, keyworded, valid);
        FilterResult result = vacancyFilter.filterVacancies(allVacancies, keywordsToExclude);

        assertEquals(1, result.filteredVacancies().size());
        assertEquals(1, result.vacanciesWithTests().size());
    }


    public VacancyItem createVacancy(String name, boolean hasTest, boolean archived, List<VacancyRelation> relations) {
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
                relations,
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
