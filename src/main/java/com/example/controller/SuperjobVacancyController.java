package com.example.controller;

import com.example.dto.superjob.SuperjobVacancyRequest;
import com.example.dto.superjob.Vacancy;
import com.example.model.AuthUser;
import com.example.service.superjob.ClientSuperjob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/superjob/vacancies")
public class SuperjobVacancyController {
    private final ClientSuperjob clientSuperjob;

    @PostMapping("/all")
    public Set<Vacancy> getAllVacancies(
            @RequestBody SuperjobVacancyRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return clientSuperjob.getVacancySuperjob(authUser.getUser(), request);
    }

    @PostMapping("/apply-to-vacancies")
    public ResponseEntity<Void> applyToVacancies(
            @RequestBody SuperjobVacancyRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        clientSuperjob.sendCvOnVacancy(request, authUser.getUser());
        return ResponseEntity.ok().build();
    }
}
