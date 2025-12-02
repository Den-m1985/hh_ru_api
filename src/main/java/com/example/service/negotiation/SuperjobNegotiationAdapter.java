package com.example.service.negotiation;

import com.example.dto.negotiation.SuperjobNegotiation;
import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
public class SuperjobNegotiationAdapter implements ExternalNegotiation {

    private final SuperjobNegotiation src;

    @Override
    public String externalId() {
        return src.id_vacancy() + "_" + src.id_resume() + "_" + src.date_sent();
    }

    @Override
    public ApiProvider provider() {
        return ApiProvider.SUPERJOB;
    }

    @Override
    public LocalDateTime createdAt() {
        return unixToLocalDateTime(src.date_sent());
    }

    @Override
    public String companyName() {
        return src.firm_name();
    }

    @Override
    public String vacancyUrl() {
        return src.vacancy().link();
    }

    @Override
    public String positionName() {
        return src.position_name();
    }

    @Override
    public Boolean viewed() {
        return src.date_viewed() != null;
    }

    @Override
    public NegotiationState externalState() {
        return mapSjStatus(src.status());
    }

    private LocalDateTime unixToLocalDateTime(long unix) {
        return Instant.ofEpochSecond(unix)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private NegotiationState mapSjStatus(Integer status) {
        return switch (status) {
            case 1 -> NegotiationState.INVITATION;
            case 2, 30 -> NegotiationState.DISCARD; // в доке не написано, но 30 отклонено
            default -> NegotiationState.RESPONSE; // response = не просмотрено
        };
    }
}
