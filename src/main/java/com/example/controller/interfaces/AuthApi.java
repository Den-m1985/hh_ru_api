package com.example.controller.interfaces;

import com.example.dto.AuthResponse;
import com.example.dto.JwtAuthResponse;
import com.example.dto.UserDTO;
import com.example.dto.user.UserInfoDto;
import com.example.model.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication Controller")
public interface AuthApi {

    @Operation(summary = "User Registration")
    ResponseEntity<AuthResponse> registerUser(@RequestBody @Valid UserDTO request, HttpServletResponse response);

    @Operation(summary = "User Login")
    ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid UserDTO authRequest, HttpServletResponse response);

    @Operation(summary = "Get user info")
    ResponseEntity<UserInfoDto> getCurrentUser(@AuthenticationPrincipal AuthUser authUser);

    @Operation(summary = "User Logout")
    ResponseEntity<Void> logout(HttpServletResponse response);
}
