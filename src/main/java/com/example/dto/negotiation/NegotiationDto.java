package com.example.dto.negotiation;

import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;

import java.time.LocalDateTime;

public class NegotiationDto {
    Long id;
    NegotiationState state;
    Boolean viewedByOpponent;
    ApiProvider apiProvider;
    LocalDateTime sendAt;
    String comment;
    String vacancyUrl;
    String companyName;
    String positionName;
}
