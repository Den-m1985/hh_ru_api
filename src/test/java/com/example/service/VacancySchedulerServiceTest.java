package com.example.service;

import com.example.dto.AuthResponse;
import com.example.dto.UserDTO;
import com.example.dto.VacancyRequest;
import com.example.model.AuthUser;
import com.example.model.AutoResponseSchedule;
import com.example.model.HhToken;
import com.example.model.User;
import com.example.repository.AutoResponseScheduleRepository;
import com.example.repository.UserRepository;
import com.example.service.common.RegisterService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class VacancySchedulerServiceTest {
    @Autowired
    private RegisterService registerService;
    @Autowired
    private HhTokenService hhTokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VacancySchedulerService scheduler;
    @Autowired
    private AutoResponseScheduleRepository scheduleRepository;
    @MockitoBean
    private VacancyResponseProcessor processor; // << замена настоящего на мок
    private final HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    private AuthUser testAuthUser;
    private User testUser;
    private VacancyRequest vacancyRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        scheduleRepository.deleteAll();

        String email = "john.doe@example.com";
        String password = "password";
        UserDTO request = new UserDTO(email, password);
        AuthResponse response = registerService.registerUser(request, servletResponse);
        assertNotNull(response.userId());
        testUser = userRepository.findByEmail(email).orElse(null);

        HhToken hhToken = new HhToken();
        hhToken.setAccessToken("test_access_token");
        hhToken.setExpiresIn(10000L);
        hhToken.setUser(testUser);
        hhTokenService.saveToken(hhToken);
        assertNotNull(testUser);
        testUser.setHhToken(hhToken);

        testAuthUser = new AuthUser(testUser);

        vacancyRequest = initVacancyRequest();
    }


    private VacancyRequest initVacancyRequest() {
        return new VacancyRequest(
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


    @Test
    void shouldCreateScheduleAndCacheIt() {
        // обновляем планировщик
        scheduler.updateSchedule(vacancyRequest, testAuthUser);

        // проверяем, что запись появилась
        AutoResponseSchedule schedule = scheduleRepository.findByUserId(testUser.getId()).orElse(null);
        assertNotNull(schedule);
        assertTrue(schedule.isEnabled());
        assertEquals(testUser.getId(), schedule.getUser().getId());
    }

    @Test
    void shouldCallProcessorForEachActiveSchedule() {

        AutoResponseSchedule schedule = new AutoResponseSchedule();
        schedule.setUser(testUser);
        schedule.setEnabled(true);
        schedule.setParams(vacancyRequest);
        scheduleRepository.save(schedule);

        scheduler.init();
        scheduler.executeScheduledResponses();

        verify(processor).respondToRelevantVacancies(vacancyRequest, testUser.getId()); // << проверка вызова
    }
}
