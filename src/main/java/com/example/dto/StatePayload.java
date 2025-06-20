package com.example.dto;

import com.example.enums.Device;

public record StatePayload(
        Integer userId,
        Device device
) {
}
