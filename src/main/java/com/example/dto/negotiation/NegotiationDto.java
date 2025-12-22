package com.example.dto.negotiation;

import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NegotiationDto {
    Integer id;
    NegotiationState state;
    Boolean viewedByOpponent;
    ApiProvider apiProvider;
    LocalDateTime sendAt;
    String comment;
    String vacancyUrl;
    String companyName;
    String positionName;
}
