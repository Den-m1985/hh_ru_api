package com.example.service;

import com.example.service.auth.OAuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallBackService {
    private final OAuthClient oauthClient;
    private final ResumeService resumeService;
    private final VacancyResponseProcessor vacancyService;
    private final SavedSearches savedSearches;
    private final NegotiationsAll negotiationsAll;

    public void processApp(String code) {
        log.debug("Получен код: {}", code);

        oauthClient.authenticate(code);

        resumeService.getResumeFromHh();

        negotiationsAll.getNegotiations();

        vacancyService.respondToRelevantVacancies();

//        savedSearches.getSavedSearches();
    }
}
