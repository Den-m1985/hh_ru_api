package com.example.dto;

import com.example.enums.Gender;

public record UserInfoDto(
        String firstName,
        String middleName,
        String lastName,
        String email,
        Gender gender,
        String role
) {
}
