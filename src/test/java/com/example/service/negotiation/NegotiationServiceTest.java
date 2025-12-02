package com.example.service.negotiation;

import com.example.RedisTestConfig;
import com.example.dto.HeadhunterNegotiation;
import com.example.dto.negotiation.NegotiationDto;
import com.example.dto.negotiation.NegotiationRequestDto;
import com.example.dto.negotiation.NegotiationStatistic;
import com.example.dto.negotiation.SuperjobNegotiation;
import com.example.dto.superjob.Vacancy;
import com.example.dto.vacancy_dto.Employer;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import com.example.model.Negotiation;
import com.example.model.RoleEnum;
import com.example.model.User;
import com.example.repository.NegotiationRepository;
import com.example.repository.UserRepository;
import com.example.service.VacancyClient;
import com.example.service.superjob.ClientSuperjob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
public class NegotiationServiceTest {
    @Autowired
    private NegotiationService negotiationService;
    @Autowired
    private NegotiationRepository negotiationRepository;
    @Autowired
    private UserRepository userRepository;
    @MockitoBean
    private VacancyClient vacancyClient;
    @MockitoBean
    private ClientSuperjob superjobClient;
    @MockitoBean
    private RedisKeyCommands keyCommands;
    private User user;

    @BeforeEach
    void setUp() {
        negotiationRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setRole(RoleEnum.USER);
        User savedUser = userRepository.save(user);
        user.setId(savedUser.getId());
    }

    @Test
    void getUserStatistic_initialSync_triggeredOnce() {
        HeadhunterNegotiation hh = HeadhunterNegotiation.builder()
                .id("id")
                .created_at(LocalDateTime.now())
                .state(NegotiationState.RESPONSE)
                .viewed_by_opponent(false)
                .vacancy(
                        VacancyItem.builder()
                                .employer(
                                        Employer.builder()
                                                .name("employerName")
                                                .build())
                                .alternate_url("alternateUrl")
                                .name("positionName")
                                .build()
                )
                .build();
        when(vacancyClient.getAllNegotiations(user)).thenReturn(List.of(hh));

        SuperjobNegotiation sj = SuperjobNegotiation.builder()
                .id_vacancy(1L)
                .id_resume(2L)
                .date_sent(3L)
                .firm_name("firm_name")
                .vacancy(Vacancy.builder()
                        .link("link")
                        .build())
                .status_text("status_text")
                .position_name("position_name")
                .status(3)
                .build();
        when(superjobClient.getAllNegotiations(user)).thenReturn(List.of(sj));

        when(keyCommands.exists(any(byte[].class)))
                .thenReturn(false);
        NegotiationStatistic result = negotiationService.getUserStatistic(user);

        assertNotNull(result);

        System.out.println(negotiationRepository.findAll());
        List<Negotiation> stored = negotiationRepository.findAll();
        assertEquals(2, stored.size());
    }

    @Test
    void getNegotiations_paginationWorks() {
        Negotiation negotiation = new Negotiation();
        negotiation.setExternalId("1");
        negotiation.setProvider(ApiProvider.HEADHUNTER);
        negotiation.setUser(user);
        negotiation.setSendAt(LocalDateTime.now());

        Negotiation negotiation2 = new Negotiation();
        negotiation2.setExternalId("2");
        negotiation2.setProvider(ApiProvider.SUPERJOB);
        negotiation2.setUser(user);
        negotiation2.setSendAt(LocalDateTime.now());

        when(keyCommands.exists(any(byte[].class)))
                .thenReturn(true);

        System.out.println(negotiation);
        negotiationRepository.saveAll(List.of(negotiation, negotiation2));

        List<NegotiationDto> result = negotiationService.getNegotiations(user, 0, 1);
        assertEquals(1, result.size());
        assertEquals(ApiProvider.HEADHUNTER, result.get(0).getApiProvider());
    }

    @Test
    void updateNegotiation_success() {
        Negotiation negotiation = new Negotiation();
        negotiation.setExternalId("1");
        negotiation.setProvider(ApiProvider.HEADHUNTER);
        negotiation.setUser(user);
        negotiation.setSendAt(LocalDateTime.now());
        Integer negotiationId = negotiationRepository.save(negotiation).getId();

        NegotiationRequestDto request = new NegotiationRequestDto(
                negotiationId,
                NegotiationState.INTERVIEW,
                "comment");

        NegotiationDto result = negotiationService.updateNegotiation(user, request);

        assertEquals(request.state(), result.getState());
        assertEquals(request.comment(), result.getComment());
    }

    @Test
    void updateNegotiation_notFound() {
        NegotiationRequestDto dto = new NegotiationRequestDto(
                999,
                NegotiationState.INVITATION,
                "comment"
        );
        assertThrows(RuntimeException.class, () -> negotiationService.updateNegotiation(user, dto));
    }

    @Test
    void syncNegotiations_savesBothPlatforms() {
        when(vacancyClient.getAllNegotiations(user)).thenReturn(
                List.of(HeadhunterNegotiation.builder()
                        .id("id")
                        .created_at(LocalDateTime.now())
                        .state(NegotiationState.RESPONSE)
                        .viewed_by_opponent(false)
                        .vacancy(
                                VacancyItem.builder()
                                        .employer(
                                                Employer.builder()
                                                        .name("employerName")
                                                        .build())
                                        .alternate_url("alternateUrl")
                                        .name("positionName")
                                        .build()
                        )
                        .build())
        );

        when(superjobClient.getAllNegotiations(user)).thenReturn(
                List.of(SuperjobNegotiation.builder()
                        .id_vacancy(1L)
                        .id_resume(2L)
                        .date_sent(3L)
                        .firm_name("firm_name")
                        .vacancy(Vacancy.builder()
                                .link("link")
                                .build())
                        .status_text("status_text")
                        .position_name("position_name")
                        .status(3)
                        .build())
        );

        negotiationService.getUserStatistic(user);

        List<Negotiation> stored = negotiationRepository.findAll();
        assertEquals(2, stored.size());
    }

    @Test
    void saveOrUpdateHeadhunterNegotiations_updatesStateOnlyIfAllowed() {
        Negotiation existing = new Negotiation();
        existing.setExternalId("123");
        existing.setProvider(ApiProvider.HEADHUNTER);
        existing.setUser(user);
        existing.setSendAt(LocalDateTime.now());
        existing.setState(NegotiationState.OFFER); // не должно обновиться так как пользователь сам задал поле
        existing.setViewedByOpponent(true);
        negotiationRepository.save(existing);

        HeadhunterNegotiation incoming = HeadhunterNegotiation.builder()
                .id("id")
                .created_at(LocalDateTime.now())
                .state(NegotiationState.RESPONSE)
                .viewed_by_opponent(false)
                .vacancy(
                        VacancyItem.builder()
                                .employer(
                                        Employer.builder()
                                                .name("employerName")
                                                .build())
                                .alternate_url("alternateUrl")
                                .name("positionName")
                                .build()
                )
                .build();

        when(vacancyClient.getAllNegotiations(user)).thenReturn(List.of(incoming));
        when(superjobClient.getAllNegotiations(user)).thenReturn(List.of());

        negotiationService.getUserStatistic(user);

        Negotiation updated = negotiationRepository.findAll().get(0);
        assertEquals(NegotiationState.OFFER, updated.getState());
    }

    @Test
    void saveOrUpdateSuperjobNegotiations_createsCorrectExternalId() {
        when(vacancyClient.getAllNegotiations(user)).thenReturn(List.of());
        when(superjobClient.getAllNegotiations(user)).thenReturn(
                List.of(SuperjobNegotiation.builder()
                        .id_vacancy(1L)
                        .id_resume(2L)
                        .date_sent(3L)
                        .firm_name("firm_name")
                        .vacancy(Vacancy.builder()
                                .link("link")
                                .build())
                        .status_text("status_text")
                        .position_name("position_name")
                        .status(2)
                        .build())
        );

        negotiationService.getUserStatistic(user);

        Negotiation n = negotiationRepository.findAll().get(0);

        assertEquals("1_2_3", n.getExternalId());
        assertEquals(NegotiationState.DISCARD, n.getState());
    }


    @Test
    void canUpdateState_logicCorrect() {
        Negotiation n = new Negotiation();

        n.setState(null);
        assertTrue(callCanUpdateState(n));

        n.setState(NegotiationState.RESPONSE);
        assertTrue(callCanUpdateState(n));

        n.setState(NegotiationState.DISCARD_AFTER_INTERVIEW);
        assertFalse(callCanUpdateState(n));
    }

    private boolean callCanUpdateState(Negotiation n) {
        try {
            var m = NegotiationService.class.getDeclaredMethod("canUpdateState", Negotiation.class);
            m.setAccessible(true);
            return (boolean) m.invoke(negotiationService, n);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
