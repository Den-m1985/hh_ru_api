package com.example.service.negotiation;

import com.example.dto.HeadhunterNegotiation;
import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class HeadhunterNegotiationAdapter implements ExternalNegotiation {

    private final HeadhunterNegotiation src;

    @Override
    public String externalId() {
        return src.id();
    }

    @Override
    public ApiProvider provider() {
        return ApiProvider.HEADHUNTER;
    }

    @Override
    public LocalDateTime createdAt() {
        return src.created_at();
    }

    @Override
    public String companyName() {
        return src.vacancy().employer().name();
    }

    @Override
    public String vacancyUrl() {
        return src.vacancy().alternate_url();
    }

    @Override
    public String positionName() {
        return src.vacancy().name();
    }

    @Override
    public Boolean viewed() {
        return src.viewed_by_opponent();
    }

    @Override
    public NegotiationState externalState() {
        return src.state();
    }
}
