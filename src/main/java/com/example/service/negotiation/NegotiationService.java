package com.example.service.negotiation;

import com.example.dto.negotiation.NegotiationDto;
import com.example.dto.negotiation.NegotiationRequestDto;
import com.example.dto.negotiation.NegotiationStatistic;
import com.example.dto.negotiation.PlatformStatistic;
import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import com.example.exceptions.NotFoundException;
import com.example.mapper.NegotiationMapper;
import com.example.model.Negotiation;
import com.example.model.User;
import com.example.repository.NegotiationRepository;
import com.example.service.VacancyClient;
import com.example.service.superjob.ClientSuperjob;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NegotiationService {
    NegotiationRepository negotiationRepository;
    VacancyClient vacancyClient;
    ClientSuperjob superjobClient;
    RedisTemplate<String, Object> redisTemplate;
    static String LAST_SYNC = "user:sync:";
    static Duration TTL = Duration.ofMinutes(5);
    static Set<NegotiationState> SYSTEM_STATUSES = Set.of(
            NegotiationState.RESPONSE,
            NegotiationState.INVITATION,
            NegotiationState.DISCARD,
            NegotiationState.HIDDEN
    );

    @Transactional
    public NegotiationStatistic getUserStatistic(User user) {
        log.info("Get user statistic. User: {}", user.getId());
        ensureSynced(user);
        List<Negotiation> negotiations = negotiationRepository.findAllByUser(user);
        return calculateStatistic(negotiations);
    }

    @Transactional
    public List<NegotiationDto> getNegotiations(User user, Integer from, Integer size) {
        log.info("Get negotiations from {} to {}. User {}", from, size, user.getId());
        ensureSynced(user);
        List<Negotiation> negotiations = negotiationRepository.findAllByUserOrderBySendAtDesc(user, PageRequest.of(from, size));
        return NegotiationMapper.toDto(negotiations);
    }

    @Transactional
    public NegotiationDto updateNegotiation(User user, NegotiationRequestDto negotiationRequestDto) {
        log.info("Update negotiation. User {}, request: {}", user.getId(), negotiationRequestDto);
        Optional<Negotiation> negotiation = negotiationRepository.findByIdAndUser(negotiationRequestDto.id(), user);
        if (negotiation.isEmpty()) {
            log.warn("Negotiation with id {} not found. User: {}", negotiationRequestDto.id(), user.getId());
            throw new NotFoundException("Negotiation with id " + negotiationRequestDto.id() + " not found");
        }
        Negotiation existingNegotiation = negotiation.get();
        existingNegotiation.setState(negotiationRequestDto.state() != null ? negotiationRequestDto.state() : existingNegotiation.getState());
        existingNegotiation.setComment(negotiationRequestDto.comment() != null ? negotiationRequestDto.comment() : existingNegotiation.getComment());
        return NegotiationMapper.toDto(existingNegotiation);
    }

    private NegotiationStatistic calculateStatistic(List<Negotiation> negotiations) {
        Map<ApiProvider, List<Negotiation>> grouped = negotiations.stream()
                .collect(Collectors.groupingBy(Negotiation::getProvider));

        return new NegotiationStatistic(
                buildStat(grouped.getOrDefault(ApiProvider.HEADHUNTER, List.of())),
                buildStat(grouped.getOrDefault(ApiProvider.SUPERJOB, List.of())),
                buildStat(negotiations)
        );
    }

    private PlatformStatistic buildStat(List<Negotiation> negotiations) {
        long total = negotiations.size();
        long views = negotiations.stream()
                .filter(Negotiation::getViewedByOpponent)
                .count();
        long responses = negotiations.stream()
                .filter(n -> !n.getState().equals(NegotiationState.RESPONSE))
                .count();
        long invites = negotiations.stream()
                .filter(n -> n.getState().equals(NegotiationState.INVITATION))
                .count();
        long passed = negotiations.stream()
                .filter(n -> n.getState().equals(NegotiationState.INTERVIEW_PASSED))
                .count();
        long offer = negotiations.stream()
                .filter(n -> n.getState().equals(NegotiationState.OFFER))
                .count();
        long noAnswer = negotiations.stream()
                .filter(n -> n.getState().equals(NegotiationState.RESPONSE))
                .count();
        long discardByResume = negotiations.stream()
                .filter(n -> n.getState().equals(NegotiationState.DISCARD))
                .count();
        long discardAfterInterview = negotiations.stream()
                .filter(n -> n.getState().equals(NegotiationState.DISCARD_AFTER_INTERVIEW))
                .count();
        return new PlatformStatistic(total, views, responses, invites, offer, noAnswer, discardByResume, discardAfterInterview, passed);
    }

    @Transactional
    void syncNegotiations(User user) {
        log.info("Sync negotiations. User {}", user);
        List<HeadhunterNegotiationAdapter> hhNegotiations = vacancyClient.getAllNegotiations(user)
                .stream()
                .map(HeadhunterNegotiationAdapter::new)
                .collect(Collectors.toList());
        List<SuperjobNegotiationAdapter> sjNegotiations = superjobClient.getAllNegotiations(user)
                .stream()
                .map(SuperjobNegotiationAdapter::new)
                .collect(Collectors.toList());
        syncExternalNegotiations(user, hhNegotiations);
        syncExternalNegotiations(user, sjNegotiations);
    }

    @Transactional
    void syncExternalNegotiations(User user, List<? extends ExternalNegotiation> externalNegotiations) {
        if (externalNegotiations.isEmpty()) {
            return;
        }
        ApiProvider provider = externalNegotiations.get(0).provider();
        List<Negotiation> existing = negotiationRepository.findAllByUserAndProvider(user, provider);
        Map<String, Negotiation> existingMap = existing.stream()
                .collect(Collectors.toMap(Negotiation::getExternalId, n -> n));
        List<Negotiation> toSave = new ArrayList<>();

        for (ExternalNegotiation ext : externalNegotiations) {
            Negotiation negotiation = existingMap.get(ext.externalId());

            if (negotiation == null) {
                negotiation = new Negotiation();
                negotiation.setExternalId(ext.externalId());
                negotiation.setProvider(provider);
                negotiation.setUser(user);
                negotiation.setSendAt(ext.createdAt());
                existingMap.put(ext.externalId(), negotiation);
            }
            negotiation.setCompanyName(ext.companyName());
            negotiation.setVacancyUrl(ext.vacancyUrl());
            negotiation.setPositionName(ext.positionName());
            negotiation.setViewedByOpponent(ext.viewed());

            if (canUpdateState(negotiation)) {
                negotiation.setState(ext.externalState());
            }
            toSave.add(negotiation);
        }
        negotiationRepository.saveAll(toSave);
    }

    private boolean canUpdateState(Negotiation negotiation) {
        return negotiation.getState() == null || SYSTEM_STATUSES.contains(negotiation.getState());
    }

    @Transactional
    void ensureSynced(User user) {
        String key = LAST_SYNC + user.getId();
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, "synced", TTL);
            syncNegotiations(user);
        }
    }
}
