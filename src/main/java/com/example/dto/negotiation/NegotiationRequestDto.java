package com.example.dto.negotiation;

import com.example.enums.NegotiationState;

public record NegotiationRequestDto(
        Integer id,
        NegotiationState negotiationState,
        String comment
) {
}
