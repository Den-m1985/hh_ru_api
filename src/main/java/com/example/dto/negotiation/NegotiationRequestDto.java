package com.example.dto.negotiation;

import com.example.enums.NegotiationState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NegotiationRequestDto(
        @NotBlank
        String comment,

        @NotNull
        Integer id,

        NegotiationState state
) {
}
