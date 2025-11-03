package com.example.dto;

import com.example.dto.superjob.SuperjobVacancyRequest;
import com.example.model.AutoResponseSchedule;

import java.io.Serializable;
import java.time.LocalDateTime;

public record AutoResponseScheduleDto(
        Integer id,
        String name,
        boolean enabled,
        Object params,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String provider
) implements Serializable {
    public static AutoResponseScheduleDto fromEntity(AutoResponseSchedule schedule) {
        return new AutoResponseScheduleDto(
                schedule.getId(),
                schedule.getName(),
                schedule.isEnabled(),
                extractParams(schedule),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt(),
                extractProvider(schedule)
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

    private static String extractProvider(AutoResponseSchedule schedule) {
            String paramType= schedule.getParamsType();
            if (paramType.equals(VacancyRequest.class.getName())){
                return "hh";
            } else if (paramType.equals(SuperjobVacancyRequest.class.getName())) {
                return "sj";
            }
            return null;
    }
}
