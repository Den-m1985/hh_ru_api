package com.example.dto.superjob;

import java.util.List;

public record Company(
        Integer id,
        String title,
        String link,
        List<Object> industry,
        String description,
        Integer vacancy_count,
        String staff_count,
        String client_logo,
        String address,
        List<Object> addresses,
        String url,
        Boolean short_reg,
        Boolean is_blocked,
        Integer registered_date,
        Town town
) {
}
