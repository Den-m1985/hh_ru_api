package com.example.dto.negotiation;

import com.example.enums.NegotiationState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NegotiationRequestDto(
        @NotNull
        Integer id,
        NegotiationState state,
        @NotBlank
        String comment
) {
}
