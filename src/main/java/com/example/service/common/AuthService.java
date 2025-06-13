package com.example.service.common;

import com.example.dto.JwtAuthResponse;
import com.example.dto.UserDTO;
import com.example.model.AuthUser;
import com.example.model.User;
import com.example.service.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.lifetime.refresh}")
    private Integer lifetimeRefreshToken;

    public JwtAuthResponse login(UserDTO request, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()));
        User user = userService.getUserByEmail(request.email());
        UserDetails userDetail = new AuthUser(user);

        final String accessToken = jwtProvider.generateAccessToken(userDetail);
        final String refreshToken = jwtProvider.generateRefreshToken(userDetail);
        refreshStorage.put(user.getEmail(), refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // only HTTPS
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(lifetimeRefreshToken))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new JwtAuthResponse(accessToken, null);
    }

    public void logout(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    public JwtAuthResponse getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                User user = userService.getUserByEmail(login);
                UserDetails userDetail = new AuthUser(user);
                final String accessToken = jwtProvider.generateAccessToken(userDetail);
                return new JwtAuthResponse(accessToken, null);
            }
        }
        throw new SignatureException("invalid token");
    }


    public JwtAuthResponse refresh(@NonNull String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {

                User user = userService.getUserByEmail(login);
                UserDetails userDetail = new AuthUser(user);

                final String accessToken = jwtProvider.generateAccessToken(userDetail);
                final String newRefreshToken = jwtProvider.generateRefreshToken(userDetail);
                refreshStorage.put(userDetail.getUsername(), newRefreshToken);
                return new JwtAuthResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }

}
