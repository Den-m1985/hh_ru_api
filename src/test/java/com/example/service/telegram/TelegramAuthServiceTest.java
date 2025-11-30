package com.example.service.telegram;

import com.example.RedisTestConfig;
import com.example.dto.AuthResponse;
import com.example.dto.JwtAuthResponse;
import com.example.dto.TelegramAuthRequest;
import com.example.dto.UserDTO;
import com.example.model.RoleEnum;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.common.RegisterService;
import com.example.service.common.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class TelegramAuthServiceTest {
    @Autowired
    TelegramAuthService telegramAuthService;
    @Autowired
    UserService userService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private UserRepository userRepository;
    private final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

    private final Long testTelegramUserId = 12345L;
    private final String testFirstName = "John";
    private final String testLastName = "Doe";
    private final String testUsername = "johndoe";
    private final Long testAuthDate = System.currentTimeMillis() / 1000;
    private final String testHash = "valid_hash";
    private TelegramAuthRequest validRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        validRequest = new TelegramAuthRequest(
                testTelegramUserId,
                testFirstName,
                testLastName,
                testUsername,
                null,
                testAuthDate,
                testHash
        );
    }

    @Test
    void shouldAuthenticateAndCreateNewUser() {
        try (MockedStatic<TelegramAuthValidator> mockedStatic = Mockito.mockStatic(TelegramAuthValidator.class)) {
            mockedStatic.when(() -> TelegramAuthValidator.isValid(any(), any())).thenReturn(true);

            JwtAuthResponse authResponse = telegramAuthService.authenticate(validRequest, servletResponse);

            User createdUser = userService.getUserByUsername(testUsername);
            assertNotNull(authResponse.getAccessToken());
            assertNotNull(createdUser);
            assertEquals(testUsername, createdUser.getUsername());
            assertEquals(testTelegramUserId, createdUser.getTelegramUserId());
            assertEquals(RoleEnum.USER, createdUser.getRole());
        }
    }

    @Test
    void shouldAuthenticateExistingUser() {
        try (MockedStatic<TelegramAuthValidator> mockedStatic = Mockito.mockStatic(TelegramAuthValidator.class)) {
            mockedStatic.when(() -> TelegramAuthValidator.isValid(any(), any())).thenReturn(true);

            String email = "john.doe@example.com";
            String password = "password";
            UserDTO request = new UserDTO(email, password);
            AuthResponse response = registerService.registerUser(request, servletResponse);
            User testUser = userRepository.findByEmail(email).orElse(null);
            assertNotNull(testUser);
            testUser.setTelegramUserId(testTelegramUserId);
            userService.saveUser(testUser);
            assertNotNull(response.userId());

            JwtAuthResponse authResponse = telegramAuthService.authenticate(validRequest, servletResponse);

            assertNotNull(authResponse.getAccessToken());
            assertEquals(1, userRepository.count());
        }
    }

    @Test
    void shouldHandleUserWithoutUsername() {
        try (var mockedStatic = Mockito.mockStatic(TelegramAuthValidator.class)) {
            mockedStatic.when(() -> TelegramAuthValidator.isValid(any(), any())).thenReturn(true);
            TelegramAuthRequest requestWithoutUsername = new TelegramAuthRequest(
                    testTelegramUserId,
                    testFirstName,
                    testLastName,
                    null, // username null
                    null,
                    testAuthDate,
                    testHash
            );

            JwtAuthResponse authResponse = telegramAuthService.authenticate(requestWithoutUsername, servletResponse);

            User createdUser = userService.getUserByUsername("telegram_" + testTelegramUserId);
            assertNotNull(authResponse.getAccessToken());
            assertNotNull(createdUser.getUsername());
            assertTrue(createdUser.getUsername().startsWith("telegram_"));
        }
    }

    @Test
    void shouldThrowExceptionForInvalidTelegramAuthData() {
        try (var mockedStatic = Mockito.mockStatic(TelegramAuthValidator.class)) {
            mockedStatic.when(() -> TelegramAuthValidator.isValid(any(), any())).thenReturn(false);

            assertThrows(
                    org.springframework.security.authentication.BadCredentialsException.class,
                    () -> telegramAuthService.authenticate(validRequest, servletResponse)
            );
        }
    }

}
