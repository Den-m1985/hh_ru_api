package com.example.dto;

import com.example.model.AutoResponseSchedule;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AutoResponseScheduleDto(
        Integer id,
        String name,
        boolean enabled,
        Object params,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
    public static AutoResponseScheduleDto fromEntity(AutoResponseSchedule schedule) {
        return new AutoResponseScheduleDto(
                schedule.getId(),
                schedule.getName(),
                schedule.isEnabled(),
                extractParams(schedule),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    private static Object extractParams(AutoResponseSchedule schedule) {
        try {
            Class<?> clazz = Class.forName(schedule.getParamsType());
            return schedule.getParams(clazz).orElse(null);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
