package com.example.controller;

import com.example.dto.JwtAuthResponse;
import com.example.dto.TelegramAuthRequest;
import com.example.model.AuthUser;
import com.example.service.telegram.TelegramAuthService;
import com.example.service.telegram.TelegramLinkService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/telegram")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramLinkService telegramLinkService;
    private final TelegramAuthService telegramAuthService;

    @GetMapping("/link")
    public ResponseEntity<String> generateLinkCode(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(telegramLinkService.generateLinkCode(authUser.getUser().getId()));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtAuthResponse> authViaTelegram(
            @RequestBody TelegramAuthRequest telegramAuthRequest,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(telegramAuthService.authenticate(telegramAuthRequest, response));
    }
}
