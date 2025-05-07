package com.example.dto;

import com.example.dto.vacancy_dto.VacancyItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NegotiationItemDto(
        String id,
        Object state,
        String created_at,
        String updated_at,
        Object resume,
        Boolean viewed_by_opponent,
        Boolean has_updates,
        String messages_url,
        String url,
        Object counters,
        Object chat_states,
        String source,
        String chat_id,
        String messaging_status,
        Boolean decline_allowed,
        Boolean read,
        Boolean has_new_messages,
        Boolean applicant_question_state,
        Boolean hidden,
        VacancyItem vacancy
) {
}
