package com.example.controller;

import com.example.controller.interfaces.HeadHunterApi;
import com.example.dto.CodeDto;
import com.example.dto.vacancy_dto.ResumeItemDto;
import com.example.model.AuthUser;
import com.example.service.common.ResumeService;
import com.example.service.HhTokenService;
import com.example.service.OAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/hh_ru")
public class HeadHunterController implements HeadHunterApi {
    private final OAuthClient oauthClient;
    private final ResumeService resumeService;
    private final HhTokenService tokenService;

    @GetMapping("/get_auth_url")
    public ResponseEntity<String> getAuthUrl() {
        return ResponseEntity.ok(oauthClient.getAuthorizeUrl());
    }

    @PostMapping("/code_hh")
    public ResponseEntity<String> code(@RequestBody CodeDto code, @AuthenticationPrincipal AuthUser authUser) {
        oauthClient.authenticate(code.code(), authUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/resume")
    public ResponseEntity<List<ResumeItemDto>> getMineResume(@AuthenticationPrincipal AuthUser authUser) {
        List<ResumeItemDto> data = resumeService.getResumesFromHh(authUser);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/is_token")
    public ResponseEntity<Boolean> isTokenGood(/*@RequestBody ResumeDto resumeId, */@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(tokenService.checkToken(/*resumeId, */authUser));
    }

    @PostMapping("/token")
    public ResponseEntity<Boolean> refreshTokens() {
        return ResponseEntity.ok(tokenService.refreshTokens());
    }
}
