package com.example.controller;

import com.example.controller.interfaces.SchedulerApi;
import com.example.dto.AutoResponseScheduleDto;
import com.example.dto.VacancyRequest;
import com.example.dto.superjob.SuperjobVacancyRequest;
import com.example.model.AuthUser;
import com.example.service.VacancySchedulerService;
import com.example.service.superjob.SuperjobSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/scheduler")
public class SchedulerController implements SchedulerApi {
    private final VacancySchedulerService vacancySchedulerService;
    private final SuperjobSchedulerService superjobSchedulerService;

    @PostMapping("/auto")
    public ResponseEntity<AutoResponseScheduleDto> triggerAutoResponse(
            @RequestBody VacancyRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(vacancySchedulerService.createOrUpdateSchedule(request, authUser.getUser()));
    }

    // TODO надо применить Стратегию и фабричный метод с реализацией общих методов.
    @PostMapping("/superjob/auto")
    public ResponseEntity<AutoResponseScheduleDto> superjobAutoResponse(
            @RequestBody SuperjobVacancyRequest request,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(superjobSchedulerService.createOrUpdateSchedule(request, authUser.getUser()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AutoResponseScheduleDto>> getAllUserSchedules(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(vacancySchedulerService.getAllSchedulesByUser(authUser.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        vacancySchedulerService.deleteScheduleById(id, authUser.getUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutoResponseScheduleDto> getScheduleById(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(vacancySchedulerService.getScheduleById(id, authUser.getUser()));
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<AutoResponseScheduleDto> getScheduleByName(
            @PathVariable String name,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(vacancySchedulerService.getScheduleByName(name, authUser.getUser()));
    }
}
