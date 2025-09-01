package com.example.dto;

import com.example.model.AutoResponseSchedule;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AutoResponseScheduleDto(
        Integer id,
        String name,
        boolean enabled,
        VacancyRequest params,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
    public static AutoResponseScheduleDto fromEntity(AutoResponseSchedule schedule) {
        return new AutoResponseScheduleDto(
                schedule.getId(),
                schedule.getName(),
                schedule.isEnabled(),
                schedule.getParams(VacancyRequest.class).orElse(null),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }
}
