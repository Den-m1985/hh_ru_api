package com.example.controller;

import com.example.controller.interfaces.VacancyHistoryApi;
import com.example.dto.VacancyHistoryDto;
import com.example.model.AuthUser;
import com.example.service.VacancyHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vacancy_history")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VacancyHistoryController implements VacancyHistoryApi {
    VacancyHistoryService historyService;


    @GetMapping("/by_provider/{provider}")
    public ResponseEntity<List<VacancyHistoryDto>> getHistoryByProvider(@PathVariable String provider) {
        return ResponseEntity.ok(historyService.getVacanciesByProvider(provider));
    }

    @GetMapping("/by_user")
    public ResponseEntity<List<VacancyHistoryDto>> getHistoryByUser(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(historyService.getVacanciesHistoryByUser(authUser.getUser().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Integer id) {
        historyService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
