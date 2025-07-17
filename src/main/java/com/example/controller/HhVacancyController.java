package com.example.controller;

import com.example.controller.interfaces.HhVacancyAPI;
import com.example.dto.VacancyRequest;
import com.example.dto.vacancy_dto.Area;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.model.AuthUser;
import com.example.service.AllVacancies;
import com.example.service.VacancyClient;
import com.example.service.VacancyResponseProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/hh/vacancies")
public class HhVacancyController implements HhVacancyAPI {
    private final AllVacancies allVacancies;
    private final VacancyResponseProcessor vacancyResponseProcessor;
    private final VacancyClient vacancyClient;

    @PostMapping("/all")
    public Set<VacancyItem> getAllVacancies(@RequestBody VacancyRequest request) {
        return allVacancies.getAllVacancies(request);
    }

    @PostMapping("/all_filter")
    public List<VacancyItem> getAllFilteredVacancies(
            @RequestBody VacancyRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return vacancyResponseProcessor.prepareData(request, authUser.getUser().getId());
    }

    @PostMapping("/apply-to-vacancies")
    public ResponseEntity<Void> applyToVacancies(
            @RequestBody VacancyRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        vacancyResponseProcessor.respondToRelevantVacancies(request, authUser.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/area")
    public ResponseEntity<List<Area>> getAreas(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(vacancyClient.getAreas(authUser.getUser()));
    }
}
