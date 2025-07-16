package com.example.service;

import com.example.dto.AuthResponse;
import com.example.dto.HhTokenResponse;
import com.example.dto.UserDTO;
import com.example.model.BaseEntity;
import com.example.model.HhToken;
import com.example.model.User;
import com.example.repository.HhTokenRepository;
import com.example.repository.UserRepository;
import com.example.service.common.RegisterService;
import com.example.util.HeadHunterProperties;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceTest {
    @Autowired
    private HhTokenService hhTokenService;
    @Autowired
    private HhTokenRepository tokenRepository;
    @Autowired
    private HeadHunterProperties headHunterProperties;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegisterService registerService;
    private final HttpServletResponse servletResponse = mock(HttpServletResponse.class);
    @MockitoBean // This will inject a Mockito mock into the Spring context
    private HhTokenRefreshClient hhTokenRefreshClient;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();

        String email = "john.doe@example.com";
        String password = "password";
        UserDTO request = new UserDTO(email, password);
        AuthResponse response = registerService.registerUser(request, servletResponse);
        assertNotNull(response.userId());
        testUser = userRepository.findByEmail(email).orElse(null);

        HhToken hhToken = new HhToken();
        hhToken.setAccessToken("test_access_token");
        hhToken.setRefreshToken("test_refresh_token");
        hhToken.setExpiresIn(10000L);
        hhToken.setUser(testUser);
        hhTokenService.saveToken(hhToken);
        assertNotNull(testUser);
        testUser.setHhToken(hhToken);
    }

    @Test
    void isTokenGood_ShouldReturnFalse_WhenTokenIsNull() {
        assertFalse(hhTokenService.isTokenGood(null));
    }

    @Test
    void isTokenGood_ShouldReturnFalse_WhenTokenFieldsAreNull() {
        assertFalse(hhTokenService.isTokenGood(new HhToken()));
    }

    @Test
    void isTokenGood_ShouldReturnFalse_WhenTokenExpired() {
        HhToken token = new HhToken();
        token.setAccessToken("valid_token");
        Duration duration = Duration.ofHours(1);
        token.setExpiresIn(duration.toSeconds());
        setUpdatedAt(token, LocalDateTime.now().minusHours(2)); // обновлен 2 часа назад

        assertFalse(hhTokenService.isTokenGood(token));
    }

    @Test
    void isTokenGood_ShouldReturnTrue_WhenTokenValid() {
        HhToken token = new HhToken();
        token.setAccessToken("valid_token");
        token.setExpiresIn(7200L); // 2 часа
        setUpdatedAt(token, LocalDateTime.now().minusHours(1)); // обновлен 1 час назад

        assertTrue(hhTokenService.isTokenGood(token));
    }

    @Test
    void isTokenGood_ShouldReturnTrue_WhenTokenExpired() {
        HhToken token = new HhToken();
        token.setAccessToken("valid_token");
        token.setExpiresIn(300L); // 5 минут
        setUpdatedAt(token, LocalDateTime.now().minusMinutes(5).minusSeconds(50));

        assertFalse(hhTokenService.isTokenGood(token));
    }

    @Test
    void isTokenGood_ShouldReturnTrue_WithCustomBuffer() {
        HhToken token = new HhToken();
        token.setAccessToken("valid_token");
        Duration duration = Duration.ofMinutes(5);
        token.setExpiresIn(duration.toSeconds());
        setUpdatedAt(token, LocalDateTime.now().minusMinutes(3).minusSeconds(30));

        assertTrue(hhTokenService.isTokenGood(token));
    }

    private void setUpdatedAt(HhToken token, LocalDateTime time) {
        try {
            Field field = BaseEntity.class.getDeclaredField("updatedAt");
            field.setAccessible(true);
            field.set(token, time);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось установить updatedAt через reflection", e);
        }
    }

    @Test
    @Transactional
    void shouldSuccessfullyRefreshHhTokenWhenExpired() {
        HhTokenResponse mockResponse = new HhTokenResponse();
        mockResponse.setAccessToken("new_access_token");
        mockResponse.setRefreshToken("new_refresh_token");
        mockResponse.setTokenType("Bearer");
        mockResponse.setExpiresIn(7200L);

        when(hhTokenRefreshClient.refreshAccessToken(anyString()))
                .thenReturn(mockResponse);
        boolean result = hhTokenService.refreshHhTokens(testUser.getHhToken());
        assertTrue(result, "Token refresh should be successful");
    }
}
