package com.example.controller;

import com.example.service.strategy.IOAuthStrategy;
import com.example.controller.interfaces.IntegrationAuthApi;
import com.example.service.factory.OAuthStrategyFactory;
import com.example.dto.interfaces.BaseResumeDto;
import com.example.model.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/{provider}")
public class ApiAuthController implements IntegrationAuthApi {
    private final OAuthStrategyFactory strategyFactory;

    @GetMapping("/get_auth_url")
    public ResponseEntity<String> getAuthUrl(@PathVariable String provider, @AuthenticationPrincipal AuthUser authUser) {
        IOAuthStrategy strategy = strategyFactory.getStrategy(provider);
        return ResponseEntity.ok(strategy.getAuthUrl(authUser));
    }

    @GetMapping("/resume")
    public ResponseEntity<List<BaseResumeDto>> getMineResume(@PathVariable String provider, @AuthenticationPrincipal AuthUser authUser) {
        IOAuthStrategy strategy = strategyFactory.getStrategy(provider);
        return ResponseEntity.ok(strategy.getMineResume(authUser));
    }

    @PostMapping("/is_token")
    public ResponseEntity<Boolean> isTokenGood(@PathVariable String provider, @AuthenticationPrincipal AuthUser authUser) {
        IOAuthStrategy strategy = strategyFactory.getStrategy(provider);
        return ResponseEntity.ok(strategy.isTokenGood(authUser));
    }
}
