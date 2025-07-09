package com.example.controller.interfaces;

import com.example.dto.AutoResponseScheduleDto;
import com.example.dto.VacancyRequest;
import com.example.model.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Scheduler API", description = "Управление расписаниями автооткликов")
public interface SchedulerApi {

    @Operation(
            summary = "Создание/обновление расписания",
            description = "Создает новое расписание или обновляет существующее по имени"
    )
    ResponseEntity<AutoResponseScheduleDto> triggerAutoResponse(
            @RequestBody VacancyRequest request,
            @AuthenticationPrincipal AuthUser authUser
    );

    @Operation(
            summary = "Получение всех расписаний пользователя",
            description = "Возвращает список всех расписаний текущего пользователя"
    )
    ResponseEntity<List<AutoResponseScheduleDto>> getAllUserSchedules(
            @AuthenticationPrincipal AuthUser authUser
    );

    @Operation(
            summary = "Удаление расписания",
            description = "Удаляет расписание по идентификатору"
    )
    ResponseEntity<Void> deleteSchedule(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthUser authUser
    );

    @Operation(
            summary = "Получение расписания по ID",
            description = "Возвращает расписание по его идентификатору"
    )
    ResponseEntity<AutoResponseScheduleDto> getScheduleById(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthUser authUser
    );

    @Operation(
            summary = "Получение расписания по имени",
            description = "Возвращает расписание по его названию"
    )
    ResponseEntity<AutoResponseScheduleDto> getScheduleByName(
            @PathVariable String name,
            @AuthenticationPrincipal AuthUser authUser
    );
}
