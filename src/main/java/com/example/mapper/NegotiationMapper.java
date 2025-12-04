package com.example.mapper;

import com.example.dto.negotiation.NegotiationDto;
import com.example.model.Negotiation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NegotiationMapper {
    public static NegotiationDto toDto(Negotiation negotiation) {
        return NegotiationDto.builder()
                .id(negotiation.getId())
                .state(negotiation.getState())
                .viewedByOpponent(negotiation.getViewedByOpponent())
                .apiProvider(negotiation.getProvider())
                .sendAt(negotiation.getSendAt())
                .comment(negotiation.getComment())
                .vacancyUrl(negotiation.getVacancyUrl())
                .companyName(negotiation.getCompanyName())
                .positionName(negotiation.getPositionName())
                .build();
    }

    public static List<NegotiationDto> toDto(List<Negotiation> negotiations) {
        return negotiations.stream()
                .map(NegotiationMapper::toDto)
                .collect(Collectors.toList());
    }
}
