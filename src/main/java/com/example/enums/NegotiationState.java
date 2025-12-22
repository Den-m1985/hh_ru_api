package com.example.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public enum NegotiationState {
    RESPONSE("response", "Отклик"),
    INVITATION("invitation", "Приглашение"),
    DISCARD("discard", "Отказ"),
    HIDDEN("hidden", "Скрытый"),
    NO_RESPONSE("no_response", "Не отвечено"),
    DISCARD_AFTER_INTERVIEW("discard_after_interview", "Отказ после приглашения"),
    INTERVIEW("interview", "Интервью"),
    INTERVIEW_PASSED("interview_passed", "Интервью пройдено"),
    OFFER("offer", "Оффер"),;

    private final String id;
    private final String name;

    NegotiationState(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonValue
    public String toJson() {
        return id;
    }

    @JsonCreator
    public static NegotiationState fromJson(@JsonProperty("id") String id) {
        for (NegotiationState state : values()) {
            if (state.id.equals(id)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown negotiation state id: " + id);
    }
}
