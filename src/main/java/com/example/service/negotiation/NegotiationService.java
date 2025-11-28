package com.example.service.negotiation;

import com.example.dto.HeadhunterNegotiation;
import com.example.dto.negotiation.*;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    static Duration TTL = Duration.ofMinutes(1);
    static Set<NegotiationState> SYSTEM_STATUSES = Set.of(
            NegotiationState.RESPONSE,
            NegotiationState.INVITATION,
            NegotiationState.DISCARD,
            NegotiationState.HIDDEN
    );

    @Transactional
    public NegotiationStatistic getUserStatistic(User user) {
        final String key = LAST_SYNC + user.getId();
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, "synced", TTL);
            syncNegotiations(user);
        }
        List<Negotiation> negotiations = negotiationRepository.findAllByUser(user);
        return calculateStatistic(negotiations);
    }

    @Transactional
    public List<NegotiationDto> getNegotiations(User user, Integer from, Integer size) {
        log.info("Get negotiations from {} to {}. User {}", from, size, user.getId());
        final String lastSyncKey = LAST_SYNC + user.getId();
        if (!redisTemplate.hasKey(lastSyncKey)) {
            redisTemplate.opsForValue().set(lastSyncKey, "synced", TTL);
            syncNegotiations(user);
        }
        List<Negotiation> negotiations = negotiationRepository.findAllByUserOrderBySendAtDesc(user, PageRequest.of(from, size));
        return NegotiationMapper.toDto(negotiations);
    }

    @Transactional
    public NegotiationDto updateNegotiation(User user, NegotiationRequestDto negotiationRequestDto) {
        Optional<Negotiation> negotiation = negotiationRepository.findById(negotiationRequestDto.id());
        if (negotiation.isEmpty() || !negotiation.get().getUser().equals(user)) {
            throw new NotFoundException("Negotiation with id " + negotiationRequestDto.id() + " not found");
        }
        Negotiation existingNegotiation = negotiation.get();
        existingNegotiation.setState(negotiationRequestDto.negotiationState() != null ? negotiationRequestDto.negotiationState() : existingNegotiation.getState());
        existingNegotiation.setComment(negotiationRequestDto.comment() != null ? negotiationRequestDto.comment() : existingNegotiation.getComment());
        return NegotiationMapper.toDto(existingNegotiation);
    }

    private NegotiationStatistic calculateStatistic(List<Negotiation> negotiations) {
        long sjTotal = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.SUPERJOB))
                .count();
        long hhTotal = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.HEADHUNTER))
                .count();
        long sjViews = negotiations.stream()
                .filter(n -> n.getViewedByOpponent() == true &&
                        n.getProvider().equals(ApiProvider.SUPERJOB))
                .count();
        long hhViews = negotiations.stream()
                .filter(n -> n.getViewedByOpponent() == true &&
                        n.getProvider().equals(ApiProvider.HEADHUNTER))
                .count();
        long sjResponses = negotiations.stream()
                .filter(n -> !n.getState().equals(NegotiationState.RESPONSE) &&
                        n.getProvider().equals(ApiProvider.SUPERJOB))
                .count();
        long hhResponses = negotiations.stream()
                .filter(n -> !n.getState().equals(NegotiationState.RESPONSE) &&
                        n.getProvider().equals(ApiProvider.HEADHUNTER))
                .count();
        long sjInterviewInvites = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.SUPERJOB) &&
                        n.getState().equals(NegotiationState.INVITATION))
                .count();
        long hhInterviewInvites = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.HEADHUNTER) &&
                        n.getState().equals(NegotiationState.INVITATION))
                .count();
        long sjPassed = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.SUPERJOB) &&
                        n.getState().equals(NegotiationState.INTERVIEW_PASSED))
                .count();
        long hhPassed = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.HEADHUNTER) &&
                        n.getState().equals(NegotiationState.INTERVIEW_PASSED))
                .count();
        long sjOffer = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.SUPERJOB) &&
                        n.getState().equals(NegotiationState.OFFER))
                .count();
        long hhOffer = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.HEADHUNTER) &&
                        n.getState().equals(NegotiationState.OFFER))
                .count();
        long sjNoAnswer = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.SUPERJOB) &&
                        n.getState().equals(NegotiationState.RESPONSE))
                .count();
        long hhNoAnswer = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.HEADHUNTER) &&
                        n.getState().equals(NegotiationState.RESPONSE))
                .count();
        long sjDiscardByResume = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.SUPERJOB) &&
                        n.getState().equals(NegotiationState.DISCARD))
                .count();
        long hhDiscardByResume = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.HEADHUNTER) &&
                        n.getState().equals(NegotiationState.DISCARD))
                .count();
        long sjDiscardAfterInterview = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.SUPERJOB) &&
                        n.getState().equals(NegotiationState.DISCARD_AFTER_INTERVIEW))
                .count();
        long hhDiscardAfterInterview = negotiations.stream()
                .filter(n -> n.getProvider().equals(ApiProvider.HEADHUNTER) &&
                        n.getState().equals(NegotiationState.DISCARD_AFTER_INTERVIEW))
                .count();
        PlatformStatistic hhStat = new PlatformStatistic(hhTotal, hhViews, hhResponses, hhInterviewInvites, hhOffer, hhNoAnswer, hhDiscardByResume, hhDiscardAfterInterview, hhPassed);
        PlatformStatistic sjStat = new PlatformStatistic(sjTotal, sjViews, sjResponses, sjInterviewInvites, sjOffer, sjNoAnswer, sjDiscardByResume, sjDiscardAfterInterview, sjPassed);
        PlatformStatistic totalStat = new PlatformStatistic(
                hhTotal + sjTotal,
                sjViews + hhViews,
                sjResponses + hhResponses,
                sjInterviewInvites + hhInterviewInvites,
                sjOffer + hhOffer,
                sjNoAnswer + hhNoAnswer,
                sjDiscardByResume + hhDiscardByResume,
                sjDiscardAfterInterview + hhDiscardAfterInterview,
                sjPassed + hhPassed);
        return new NegotiationStatistic(hhStat, sjStat, totalStat);
    }

    @Transactional
    void syncNegotiations(User user) {
        List<HeadhunterNegotiation> hhNegotiations = vacancyClient.getAllNegotiations(user);
        saveOrUpdateHeadhunterNegotiations(user, hhNegotiations);
        List<SuperjobNegotiation> sjNegotiations = superjobClient.getAllNegotiations(user);
        saveOrUpdateSuperjobNegotiations(user, sjNegotiations);
    }

    @Transactional
    void saveOrUpdateHeadhunterNegotiations(User user, List<HeadhunterNegotiation> hhNegotiations) {
        List<Negotiation> existing = negotiationRepository.findAllByUserAndProvider(user, ApiProvider.HEADHUNTER);
        Map<String, Negotiation> existingMap = existing.stream()
                .collect(Collectors.toMap(Negotiation::getExternalId, n -> n));
        List<Negotiation> toSave = new ArrayList<>();
        for (HeadhunterNegotiation hh : hhNegotiations) {
            String externalId = hh.id();
            Negotiation negotiation = existingMap.get(externalId);
            if (negotiation == null) {
                negotiation = new Negotiation();
                negotiation.setExternalId(externalId);
                negotiation.setProvider(ApiProvider.HEADHUNTER);
                negotiation.setUser(user);
                negotiation.setSendAt(hh.created_at());
                existingMap.put(externalId, negotiation);
            }
            negotiation.setCompanyName(hh.vacancy().employer().name());
            negotiation.setVacancyUrl(hh.vacancy().alternate_url());
            negotiation.setPositionName(hh.vacancy().name());
            negotiation.setViewedByOpponent(hh.viewed_by_opponent());
            if (canUpdateState(negotiation)) {
                negotiation.setState(hh.state());
            }
            toSave.add(negotiation);
        }
        negotiationRepository.saveAll(toSave);
    }

    @Transactional
    void saveOrUpdateSuperjobNegotiations(User user, List<SuperjobNegotiation> superjobNegotiations) {
        List<Negotiation> existing = negotiationRepository.findAllByUserAndProvider(user, ApiProvider.SUPERJOB);
        Map<String, Negotiation> existingMap = existing.stream()
                .collect(Collectors.toMap(Negotiation::getExternalId, n -> n));
        List<Negotiation> toSave = new ArrayList<>();
        for (SuperjobNegotiation sj : superjobNegotiations) {
            String externalId = sj.id_vacancy() + "_" + sj.id_resume() + "_" + sj.date_sent();
            Negotiation negotiation = existingMap.get(externalId);
            if (negotiation == null) {
                negotiation = new Negotiation();
                negotiation.setExternalId(externalId);
                negotiation.setProvider(ApiProvider.SUPERJOB);
                negotiation.setUser(user);
                negotiation.setSendAt(unixToLocalDateTime(sj.date_sent()));
                existingMap.put(externalId, negotiation);
            }
            negotiation.setCompanyName(sj.firm_name());
            negotiation.setVacancyUrl(sj.vacancy().link());
            negotiation.setStatusText(sj.status_text());
            negotiation.setPositionName(sj.position_name());
            negotiation.setViewedByOpponent(sj.date_viewed() != null);
            if (canUpdateState(negotiation)) {
                negotiation.setState(mapSjStatus(sj.status()));
            }
            toSave.add(negotiation);
        }
        negotiationRepository.saveAll(toSave);
    }

    private LocalDateTime unixToLocalDateTime(long unix) {
        return Instant.ofEpochSecond(unix)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private boolean canUpdateState(Negotiation negotiation) {
        return negotiation.getState() == null || SYSTEM_STATUSES.contains(negotiation.getState());
    }

    private NegotiationState mapSjStatus(Integer status) {
        return switch (status) {
            case 1 -> NegotiationState.INVITATION;
            case 2, 30 -> NegotiationState.DISCARD; // в доке не написано, но 30 отклонено
            default -> NegotiationState.RESPONSE; // response = не просмотрено
        };
    }
}
