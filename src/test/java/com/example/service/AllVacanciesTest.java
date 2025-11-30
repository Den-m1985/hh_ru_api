package com.example.service;


import com.example.dto.VacancyRequest;
import com.example.dto.vacancy_dto.*;
import com.example.util.ApplicationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

class AllVacanciesTest {
    @Mock
    private VacancyClient vacancyClient;
    @Mock
    private ApplicationProperties applicationProperties;
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
        VacancyRequest vacancyRequest = initVacancyRequest();

        Map<Integer, Integer> pageMap = new LinkedHashMap<>();
        pageMap.put(0, 2);
        pageMap.put(1, 1);

        when(paginationCalculator.calculatePages(3, 100)).thenReturn(pageMap);

        List<VacancyItem> data = initializeVacancies(3);

        when(vacancyClient.getSearchVacancies(vacancyRequest, 0, 2))
                .thenReturn(new ApiListResponse<>(List.of(data.get(0), data.get(1)), 3, 0, 2, 2, ""));
        when(vacancyClient.getSearchVacancies(vacancyRequest, 1, 1))
                .thenReturn(new ApiListResponse<>(List.of(data.get(2)), 3, 1, 2, 1, ""));

        Set<VacancyItem> result = allVacancies.getAllVacancies(vacancyRequest);

        assertThat(result).hasSize(3);
    }

    private VacancyRequest initVacancyRequest() {
        return new VacancyRequest(
                "nameSchedule",
                "12345678",         // resumeId
                3,                // count
                List.of("Senior", "Сениор", "lead", "TeamLead", "Тимлид", "Android"),
                false,
                "",
                true,

                // Основные фильтры
                "Java Developer",   // text
                "name",             // search_field
                "between1And3", // experience
                "full",             // employment
                "remote",           // schedule
                "1",                // area (например, Москва)
                "6.1",              // metro
                "96",               // professional_role (например, backend dev)
                "7",                // industry
                "1455",             // employer_id
                "RUR",              // currency
                150000L,            // salary
                "verified",         // label

                // Флаги
                true,               // only_with_salary
                30,                 // period
                "2024-06-01",       // date_from
                "2024-06-16",       // date_to

                // Геокоординаты
                55.75,              // top_lat
                55.70,              // bottom_lat
                37.65,              // left_lng
                37.70,              // right_lng

                // Сортировка
                "relevance",        // order_by
                55.72,              // sort_point_lat
                37.66,              // sort_point_lng

                // Доп. флаги
                true,               // clusters
                false,              // describe_arguments
                false,              // no_magic
                false,              // premium
                true,               // responses_count_enabled
                "part_day",         // part_time
                true,               // accept_temporary

                // Интернационализация
                "RU",               // locale
                "hh.ru"             // host
        );
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
