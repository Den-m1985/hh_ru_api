package com.example.controller;

import com.example.dto.negotiation.NegotiationDto;
import com.example.dto.negotiation.NegotiationStatistic;
import com.example.model.AuthUser;
import com.example.service.negotiation.NegotiationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/negotiation")
public class NegotiationController {
    private final NegotiationService negotiationService;

    @GetMapping("/statistic")
    public NegotiationStatistic getStatistic(@AuthenticationPrincipal AuthUser authUser) {
        return negotiationService.getUserStatistic(authUser.getUser());
    }

    @GetMapping()
    public List<NegotiationDto> getNegotiations(@AuthenticationPrincipal AuthUser authUser) {
        return negotiationService.getNegotiations(authUser.getUser());
    }

    @PostMapping
    public NegotiationDto updateNegotiation(@AuthenticationPrincipal AuthUser authUser) {
        return negotiationService.updateNegotiation(authUser.getUser());
    }
}
