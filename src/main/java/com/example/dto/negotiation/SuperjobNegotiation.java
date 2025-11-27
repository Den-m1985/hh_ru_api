package com.example.dto.negotiation;

import com.example.dto.superjob.Vacancy;

public record SuperjobNegotiation(
        String date_viewed,
        String mailId,
        Long id_vacancy,
        Vacancy vacancy,
        Long id_resume,
        String firm_name,
        String contact_face,
        Long date_sent,
        Long date_updated,
        Long date,
        String position_name,
        String type,
        String body,
        Boolean archive,
        Boolean storage,
        String resume_additional_info,
        String status_text,
        Integer status
) {
}
