package com.example.dto;

import com.example.dto.vacancy_dto.VacancyItem;
import com.example.enums.NegotiationState;
import com.example.util.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record HeadhunterNegotiation(
        String id,
        NegotiationState state,
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        LocalDateTime created_at,
        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        LocalDateTime updated_at,
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
