package com.example.service.jwt;

import com.example.RedisTestConfig;
import com.example.model.AuthUser;
import com.example.model.RoleEnum;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.common.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
class JwtProviderTest {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private final String email = "john.doe@example.com";
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setFirstName("First name");
        user.setMiddleName("Middle name");
        user.setLastName("Last name");
        user.setEmail(email);
        user.setUsername(email);
        user.setRole(RoleEnum.USER);
        user.setPassword(passwordEncoder.encode("password"));
        user = userService.saveUser(user);
        authUser = new AuthUser(user);
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        String accessToken = jwtProvider.generateAccessToken(authUser);

        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty());

        String extractUsername = jwtProvider.extractUsername(accessToken);
        assertEquals(email, extractUsername);

        Date expiration = jwtProvider.extractExpiration(accessToken);
        assertTrue(expiration.after(new Date()), "Токен должен иметь срок действия в будущем");
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        String token = jwtProvider.generateRefreshToken(authUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractUsername = jwtProvider.extractUsername(token);
        assertEquals(email, extractUsername);

        Date expiration = jwtProvider.extractExpiration(token);
        assertTrue(expiration.after(new Date()), "Токен должен иметь срок действия в будущем");
    }

    @Test
    void validateAccessToken_WithValidToken_ShouldReturnTrue() {
        String accessToken = jwtProvider.generateAccessToken(authUser);
        boolean isValid = jwtProvider.validateAccessToken(accessToken);
        assertTrue(isValid);
    }

    @Test
    void validateAccessToken_WithInvalidToken_ShouldReturnFalse() {
        String accessToken = jwtProvider.generateAccessToken(authUser);
        String corruptedToken = accessToken.substring(0, accessToken.length() - 5) + "abcde";
        boolean isValid = jwtProvider.validateAccessToken(corruptedToken);
        assertFalse(isValid);
    }

    @Test
    void validateAccessToken_WithEmptyToken_ShouldReturnFalse() {
        boolean isValid = jwtProvider.validateAccessToken("");
        assertFalse(isValid);
    }

    @Test
    void validateAccessToken_() {
        assertThrows(NullPointerException.class, () -> jwtProvider.parseClaimToken(null));
    }

    @Test
    void validateAccessToken_2() {
        assertThrows(IllegalArgumentException.class, () -> jwtProvider.parseClaimToken(""));
    }

    @Test
    void validateAccessToken_3() {
        String accessToken = jwtProvider.generateAccessToken(authUser);
        String corruptedToken = accessToken.substring(0, accessToken.length() - 5) + "abcde";
        assertThrows(SignatureException.class, () -> jwtProvider.parseClaimToken(corruptedToken));
    }

    @Test
    void validateAccessToken_4() {
        String accessToken = jwtProvider.generateTestToken(authUser);
        try {
            Thread.sleep(1100);
        } catch (InterruptedException ignored) {
        }
        assertThrows(ExpiredJwtException.class, () -> jwtProvider.parseClaimToken(accessToken));
    }
}
