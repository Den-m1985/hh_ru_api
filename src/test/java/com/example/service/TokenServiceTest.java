package com.example.service;

import com.example.model.BaseEntity;
import com.example.model.HhToken;
import com.example.repository.HhTokenRepository;
import com.example.service.common.ResumeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class TokenServiceTest {
    HhTokenService tokenService;

    @BeforeEach
    void setUp() {
        HhTokenRepository tokenRepository = mock(HhTokenRepository.class);
        ResumeService resumeService = mock(ResumeService.class);
        tokenService = new HhTokenService(tokenRepository, resumeService);
    }

    @Test
    void isTokenGood_ShouldReturnFalse_WhenTokenIsNull() {
        assertFalse(tokenService.isTokenGood(null));
    }

    @Test
    void isTokenGood_ShouldReturnFalse_WhenTokenFieldsAreNull() {
        assertFalse(tokenService.isTokenGood(new HhToken()));
    }

    @Test
    void isTokenGood_ShouldReturnFalse_WhenTokenExpired() {
        HhToken token = new HhToken();
        token.setAccessToken("valid_token");
        Duration duration = Duration.ofHours(1);
        token.setExpiresIn(duration.toSeconds());
        setUpdatedAt(token, LocalDateTime.now().minusHours(2)); // обновлен 2 часа назад

        assertFalse(tokenService.isTokenGood(token));
    }

    @Test
    void isTokenGood_ShouldReturnTrue_WhenTokenValid() {
        HhToken token = new HhToken();
        token.setAccessToken("valid_token");
        token.setExpiresIn(7200L); // 2 часа
        setUpdatedAt(token, LocalDateTime.now().minusHours(1)); // обновлен 1 час назад

        assertTrue(tokenService.isTokenGood(token));
    }

    @Test
    void isTokenGood_ShouldReturnFalse_WhenTokenAlmostExpired() {
        HhToken token = new HhToken();
        token.setAccessToken("valid_token");
        token.setExpiresIn(300L); // 5 минут
        setUpdatedAt(token, LocalDateTime.now().minusMinutes(4).minusSeconds(50)); // обновлен 4:50 назад

        assertFalse(tokenService.isTokenGood(token));
    }

    @Test
    void isTokenGood_ShouldReturnTrue_WithCustomBuffer() {
        HhToken token = new HhToken();
        token.setAccessToken("valid_token");
        Duration duration = Duration.ofMinutes(5);
        token.setExpiresIn(duration.toSeconds());
        setUpdatedAt(token, LocalDateTime.now().minusMinutes(3).minusSeconds(30));

        assertTrue(tokenService.isTokenGood(token));
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
}
