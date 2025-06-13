package com.example.service;

import com.example.model.AuthUser;
import com.example.model.Resume;
import com.example.model.User;
import com.example.service.common.ResumeService;
import com.example.service.common.UserService;
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
    private final UserService userService;

    public void processApp(String code) {
        log.debug("Получен код: {}", code);

        oauthClient.authenticate(code, new AuthUser(new User()));

        User user = userService.getUserByEmail("sender@example.com"); // TODO get user from client app

        Resume resume = resumeService.getResumeFromHh(user);

        vacancyService.respondToRelevantVacancies(resume.getHhResumeId());

        log.info("Finished");
    }
}
