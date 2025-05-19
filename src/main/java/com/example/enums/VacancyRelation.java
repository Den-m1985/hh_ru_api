package com.example.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VacancyRelation {
    FAVORITED("favorited", "В избранном"),
    GOT_RESPONSE("got_response", "Вы отправили резюме"),
    GOT_INVITATION("got_invitation", "Вас пригласили"),
    GOT_REJECTION("got_rejection", "Вам отказали"),
    BLACKLISTED("blacklisted", "Скрыта из поиска"),
    GOT_QUESTION("got_question", "Вы задали вопрос по вакансии");

    private final String id;
    private final String name;

    VacancyRelation(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Указываем, что при сериализации в JSON нужно использовать этот метод
    @JsonValue
    public String toJson() {
        return id;
    }

    // Метод для поиска по id с поддержкой десериализации
    public static VacancyRelation fromId(String id) {
        for (VacancyRelation relation : values()) {
            if (relation.id.equals(id)) {
                return relation;
            }
        }
        throw new IllegalArgumentException("Unknown vacancy relation id: " + id);
    }
}
