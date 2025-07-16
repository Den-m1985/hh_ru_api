package com.example.service.common;

import com.example.dto.AuthResponse;
import com.example.dto.UserDTO;
import com.example.model.AuthUser;
import com.example.model.RoleEnum;
import com.example.model.User;
import com.example.service.jwt.JwtProvider;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final Logger log = LoggerFactory.getLogger(RegisterService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Value("${jwt.lifetime.refresh}")
    private Integer lifetimeRefreshToken;


    public AuthResponse registerUser(UserDTO request, HttpServletResponse response) {
        Optional<User> existingUserByEmail = userService.findUserByEmail(request.email());
        if (existingUserByEmail.isPresent()) {
            throw new EntityExistsException("User already exist");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setUsername(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(RoleEnum.USER);
        user = userService.saveUser(user);
        log.info("User registered with id: {}", user.getId());

        AuthUser authUser = new AuthUser(user);
        final String accessToken = jwtProvider.generateAccessToken(authUser);
        final String refreshToken = jwtProvider.generateRefreshToken(authUser);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // only HTTPS
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(lifetimeRefreshToken))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new AuthResponse(
                accessToken,
                null,
                user.getId()
        );
    }

}
