package com.example.service;

import com.example.RedisTestConfig;
import com.example.dto.AuthResponse;
import com.example.dto.UserDTO;
import com.example.dto.VacancyHistoryDto;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.repository.VacancyHistoryRepository;
import com.example.service.common.RegisterService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class VacancyHistoryServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private VacancyHistoryService vacancyHistoryService;
    @Autowired
    private VacancyHistoryRepository vacancyHistoryRepository;

    private final HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    private User testUser;
    private final String providerHh = "hh";
    private final String email = "john.doe@example.com";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        vacancyHistoryRepository.deleteAll();

        String password = "password";
        UserDTO request = new UserDTO(email, password);
        AuthResponse response = registerService.registerUser(request, servletResponse);
        assertNotNull(response.userId());
        testUser = userRepository.findByEmail(email).orElse(null);
    }

    @Test
    void shouldThrowException() {
        assertThrows(EntityNotFoundException.class,
                () -> vacancyHistoryService.getVacancyHistoryById(1));
    }

    @Test
    void shouldSaveHistory() {
        VacancyHistoryDto dto = new VacancyHistoryDto(null, providerHh, "url", testUser.getId());
        vacancyHistoryService.addHistory(dto);

        List<VacancyHistoryDto> vacancyHistoryFromDB = vacancyHistoryService.getVacanciesByProvider(providerHh);
        assertEquals(1, vacancyHistoryFromDB.size());
        assertEquals(vacancyHistoryFromDB.get(0).provider(), providerHh);
    }

    @Test
    void shouldSaveHistoryAndLinkUser() {
        VacancyHistoryDto dto = new VacancyHistoryDto(null, providerHh, "url", testUser.getId());
        vacancyHistoryService.addHistory(dto);

        testUser = userRepository.findByEmail(email).get();
        List<VacancyHistoryDto> vacancyHistoryFromUser = vacancyHistoryService.getVacanciesHistoryByUser(testUser.getId());
        assertEquals(1, vacancyHistoryFromUser.size());
    }

    @Test
    void shouldDeleteHistory() {
        VacancyHistoryDto dto = new VacancyHistoryDto(null, providerHh, "url", testUser.getId());
        vacancyHistoryService.addHistory(dto);
        List<VacancyHistoryDto> vacancyHistoryFromDB = vacancyHistoryService.getVacanciesByProvider(providerHh);
        assertEquals(1, vacancyHistoryFromDB.size());
        vacancyHistoryService.deleteHistory(vacancyHistoryFromDB.get(0).historyId());
        List<VacancyHistoryDto> vacancyHistoryFromDBEmpty = vacancyHistoryService.getVacanciesByProvider(providerHh);
        assertEquals(0, vacancyHistoryFromDBEmpty.size());
    }
}
