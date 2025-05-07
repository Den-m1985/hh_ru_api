package com.example.dto.vacancy_dto;

public record NegotiationRequest(
        String message,
        String resumeId,
        String vacancyId
) {
}
