package com.example.service.telegram;

import com.example.dto.JwtAuthResponse;
import com.example.dto.TelegramAuthRequest;
import com.example.model.AuthUser;
import com.example.model.RoleEnum;
import com.example.model.User;
import com.example.service.common.UserService;
import com.example.service.jwt.JwtProvider;
import com.example.util.TelegramProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAuthService {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final TelegramProperties telegramProperties;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.lifetime.refresh}")
    private Integer lifetimeRefreshToken;

    @Transactional
    public JwtAuthResponse authenticate(TelegramAuthRequest req, HttpServletResponse response) {
        if (!TelegramAuthValidator.isValid(req, telegramProperties.getToken())) {
            throw new BadCredentialsException("Invalid Telegram data");
        }

        User user = getUser(req);
        AuthUser userDetail = new AuthUser(user);
        final String accessToken = jwtProvider.generateAccessToken(userDetail);
        final String refreshToken = jwtProvider.generateRefreshToken(userDetail);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // only HTTPS
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(lifetimeRefreshToken))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.info("Telegram login successful for user {}", user.getId());
        return new JwtAuthResponse(accessToken, null);
    }

    public User getUser(TelegramAuthRequest req) {
        Optional<User> user = userService.getUserByTelegramUserId(req.telegramUserId());
        if (user.isPresent()) {
            User existingUser = user.get();
            String newUsername = req.username();
            if (newUsername != null && !newUsername.equals(existingUser.getUsername())) {
                existingUser.setUsername(newUsername);
                userService.saveUser(existingUser);
            }
            return existingUser;
        }
        return createNewUser(req);
    }

    public User createNewUser(TelegramAuthRequest req) {
        User user = new User();
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        if (req.username() == null) {
            user.setUsername("telegram_" + req.telegramUserId());
        } else {
            user.setUsername(req.username());
        }
        String password = String.valueOf(req.telegramUserId());
        user.setPassword(passwordEncoder.encode(password)); // Empty password
        user.setRole(RoleEnum.USER);
        user.setTelegramUserId(req.telegramUserId());
        user = userService.saveUser(user);
        log.info("User registered with id: {}", user.getId());
        return user;
    }
}
