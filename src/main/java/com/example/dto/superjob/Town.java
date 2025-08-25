package com.example.dto.superjob;

public record Town (
        Integer id,
        String title,
        String declension,
        Boolean hasMetro,
        String genitive
){
}
