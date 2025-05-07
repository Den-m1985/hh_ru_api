package com.example.dto;

public record Vacancy(
        String id,
        String name,
        Area area,
        Employer employer
) {
}
