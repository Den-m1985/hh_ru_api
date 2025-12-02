package com.example.service.negotiation;

import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;

import java.time.LocalDateTime;

public interface ExternalNegotiation {
    String externalId();
    ApiProvider provider();
    LocalDateTime createdAt();
    String companyName();
    String vacancyUrl();
    String positionName();
    Boolean viewed();
    NegotiationState externalState();
}
