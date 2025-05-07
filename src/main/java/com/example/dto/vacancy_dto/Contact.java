package com.example.dto.vacancy_dto;

import java.util.List;

public record Contact(
        Boolean call_tracking_enabled,  // Флаг подключения виртуального номера
        String email,
        String name,
        List<Phone> phones
) {
}
