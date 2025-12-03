package com.example.service.negotiation;

import com.example.dto.HeadhunterNegotiation;
import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HeadhunterNegotiationAdapter implements ExternalNegotiation {

    HeadhunterNegotiation headhunterNegotiation;

    @Override
    public String externalId() {
        return headhunterNegotiation.id();
    }

    @Override
    public ApiProvider provider() {
        return ApiProvider.HEADHUNTER;
    }

    @Override
    public LocalDateTime createdAt() {
        return headhunterNegotiation.created_at();
    }

    @Override
    public String companyName() {
        return headhunterNegotiation.vacancy().employer().name();
    }

    @Override
    public String vacancyUrl() {
        return headhunterNegotiation.vacancy().alternate_url();
    }

    @Override
    public String positionName() {
        return headhunterNegotiation.vacancy().name();
    }

    @Override
    public Boolean viewed() {
        return headhunterNegotiation.viewed_by_opponent();
    }

    @Override
    public NegotiationState externalState() {
        return headhunterNegotiation.state();
    }
}
