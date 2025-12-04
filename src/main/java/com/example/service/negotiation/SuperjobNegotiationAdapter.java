package com.example.service.negotiation;

import com.example.dto.negotiation.SuperjobNegotiation;
import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SuperjobNegotiationAdapter implements ExternalNegotiation {

    SuperjobNegotiation superjobNegotiation;

    @Override
    public String externalId() {
        return superjobNegotiation.id_vacancy() + "_" + superjobNegotiation.id_resume() + "_" + superjobNegotiation.date_sent();
    }

    @Override
    public ApiProvider provider() {
        return ApiProvider.SUPERJOB;
    }

    @Override
    public LocalDateTime createdAt() {
        return unixToLocalDateTime(superjobNegotiation.date_sent());
    }

    @Override
    public String companyName() {
        return superjobNegotiation.firm_name();
    }

    @Override
    public String vacancyUrl() {
        return superjobNegotiation.vacancy().link();
    }

    @Override
    public String positionName() {
        return superjobNegotiation.position_name();
    }

    @Override
    public Boolean viewed() {
        return superjobNegotiation.date_viewed() != null;
    }

    @Override
    public NegotiationState externalState() {
        return mapSjStatus(superjobNegotiation.status());
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
