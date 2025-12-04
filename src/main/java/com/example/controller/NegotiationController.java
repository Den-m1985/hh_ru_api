package com.example.controller;

import com.example.controller.interfaces.NegotiationApi;
import com.example.dto.negotiation.NegotiationDto;
import com.example.dto.negotiation.NegotiationRequestDto;
import com.example.dto.negotiation.NegotiationStatistic;
import com.example.model.AuthUser;
import com.example.service.negotiation.NegotiationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/negotiation")
public class NegotiationController implements NegotiationApi {
    private final NegotiationService negotiationService;

    @GetMapping("/statistic")
    public NegotiationStatistic getStatistic(@AuthenticationPrincipal AuthUser authUser) {
        return negotiationService.getUserStatistic(authUser.getUser());
    }

    @GetMapping()
    public List<NegotiationDto> getNegotiations(@AuthenticationPrincipal AuthUser authUser,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        return negotiationService.getNegotiations(authUser.getUser(), from , size);
    }

    @PostMapping
    public NegotiationDto updateNegotiation(@AuthenticationPrincipal AuthUser authUser,
                                            @Valid @RequestBody NegotiationRequestDto negotiationRequestDto) {
        return negotiationService.updateNegotiation(authUser.getUser(), negotiationRequestDto);
    }
}
