package com.example.controller;

import com.example.dto.VacancyRequest;
import com.example.model.AuthUser;
import com.example.service.VacancySchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/scheduler")
public class SchedulerController {
    private final VacancySchedulerService vacancySchedulerService;

    @PostMapping("/auto")
    public ResponseEntity<Void> triggerAutoResponse(
            @RequestBody VacancyRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        vacancySchedulerService.updateSchedule(request, authUser);
        return ResponseEntity.ok().build();
    }
}
