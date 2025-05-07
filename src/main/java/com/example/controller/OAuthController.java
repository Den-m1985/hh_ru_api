package com.example.controller;

import com.example.model.HhToken;
import com.example.service.NegotiationsAll;
import com.example.service.ResumeService;
import com.example.service.SavedSearches;
import com.example.service.VacancyService;
import com.example.service.auth.OAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthClient oauthClient;
    private final ResumeService resumeService;
    private final VacancyService vacancyService;
    private final SavedSearches savedSearches;
    private final NegotiationsAll negotiationsAll;

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam String code) {
        System.out.println("Получен код: " + code);

        HhToken token = oauthClient.authenticate(code);

        System.out.println("Access Token: " + token.getAccessToken());

        resumeService.getResumeFromHh();

        negotiationsAll.getNegotiations();
        vacancyService.applyToVacancies(false, true);

//        savedSearches.getSavedSearches();

        return ResponseEntity.ok("token saved successfully");
    }
}
