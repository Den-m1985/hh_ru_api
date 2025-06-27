package com.example.controller;

import com.example.model.AuthUser;
import com.example.service.telegram.TelegramLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/telegram")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramLinkService telegramLinkService;

    @GetMapping("/link")
    public ResponseEntity<String> generateLinkCode(@AuthenticationPrincipal AuthUser authUser) {
        String code = telegramLinkService.generateLinkCode(authUser.getUser().getId());
        return ResponseEntity.ok(code);
    }

}
