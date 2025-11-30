package com.example.controller;

import com.example.RedisTestConfig;
import com.example.dto.negotiation.NegotiationDto;
import com.example.dto.negotiation.NegotiationRequestDto;
import com.example.dto.negotiation.NegotiationStatistic;
import com.example.dto.negotiation.PlatformStatistic;
import com.example.enums.ApiProvider;
import com.example.enums.NegotiationState;
import com.example.model.AuthUser;
import com.example.model.RoleEnum;
import com.example.model.User;
import com.example.service.negotiation.NegotiationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(RedisTestConfig.class)
public class NegotiationControllersTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NegotiationService negotiationService;

    private AuthUser authUser;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1);
        user.setUsername("name");
        user.setPassword("password");
        user.setRole(RoleEnum.USER);
        authUser = new AuthUser(user);
    }

    @Test
    @WithMockUser
    void getStatistic_success() throws Exception {
        PlatformStatistic hhStat = new PlatformStatistic(9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L);
        PlatformStatistic sjStat = new PlatformStatistic(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
        PlatformStatistic totalStat = new PlatformStatistic(
                sjStat.total() + hhStat.total(),
                sjStat.views() + hhStat.views(),
                sjStat.responses() + hhStat.responses(),
                sjStat.interviewInvites() + hhStat.interviewInvites(),
                sjStat.offers() + hhStat.offers(),
                sjStat.noAnswer() + hhStat.noAnswer(),
                sjStat.resumeRejected() + hhStat.resumeRejected(),
                sjStat.interviewRejected() + hhStat.interviewRejected(),
                sjStat.interviewPassed() + hhStat.interviewPassed()
        );
        NegotiationStatistic statistic = new NegotiationStatistic(hhStat, sjStat, totalStat);

        Mockito.when(negotiationService.getUserStatistic(any(User.class)))
                .thenReturn(statistic);

        mockMvc.perform(get("/v1/negotiation/statistic")
                        .with(SecurityMockMvcRequestPostProcessors.user(authUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hh.total").value(9L))
                .andExpect(jsonPath("$.hh.views").value(8L))
                .andExpect(jsonPath("$.hh.responses").value(7L))

                .andExpect(jsonPath("$.superjob.interviewInvites").value(4L))
                .andExpect(jsonPath("$.superjob.offers").value(5L))
                .andExpect(jsonPath("$.superjob.noAnswer").value(6L))

                .andExpect(jsonPath("$.total.resumeRejected").value(10L))
                .andExpect(jsonPath("$.total.interviewRejected").value(10L))
                .andExpect(jsonPath("$.total.interviewPassed").value(10L));
    }

    @Test
    @WithMockUser
    void updateNegotiation_success() throws Exception {
        NegotiationRequestDto request = new NegotiationRequestDto(
                10,
                NegotiationState.DISCARD_AFTER_INTERVIEW,
                "updated comment"
        );

        NegotiationDto updated = NegotiationDto.builder()
                .id(10)
                .state(NegotiationState.DISCARD_AFTER_INTERVIEW)
                .viewedByOpponent(true)
                .apiProvider(ApiProvider.HEADHUNTER)
                .sendAt(LocalDateTime.now())
                .comment("updated comment")
                .vacancyUrl("https://hh.ru/vacancy/1234567890")
                .companyName("company")
                .positionName("position")
                .build();

        Mockito.when(negotiationService.updateNegotiation(any(), any()))
                .thenReturn(updated);

        mockMvc.perform(post("/v1/negotiation")
                        .with(SecurityMockMvcRequestPostProcessors.user(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.state").value("discard_after_interview"))
                .andExpect(jsonPath("$.viewedByOpponent").value(true))
                .andExpect(jsonPath("$.apiProvider").value(ApiProvider.HEADHUNTER.name()))
                .andExpect(jsonPath("$.sendAt").isNotEmpty())
                .andExpect(jsonPath("$.comment").value("updated comment"))
                .andExpect(jsonPath("$.vacancyUrl").value("https://hh.ru/vacancy/1234567890"))
                .andExpect(jsonPath("$.companyName").value("company"))
                .andExpect(jsonPath("$.positionName").value("position"));
    }

    @Test
    @WithMockUser
    void getNegotiations_success() throws Exception {
        NegotiationDto dto1 = NegotiationDto.builder()
                .id(1)
                .state(NegotiationState.INVITATION)
                .viewedByOpponent(false)
                .apiProvider(ApiProvider.HEADHUNTER)
                .sendAt(LocalDateTime.now())
                .comment("c1")
                .vacancyUrl("https://hh.ru/vacancy/1")
                .companyName("company1")
                .positionName("position1")
                .build();

        NegotiationDto dto2 = NegotiationDto.builder()
                .id(2)
                .state(NegotiationState.INVITATION)
                .viewedByOpponent(true)
                .apiProvider(ApiProvider.SUPERJOB)
                .sendAt(LocalDateTime.now())
                .comment("c2")
                .vacancyUrl("https://superjob.ru/vacancy/2")
                .companyName("company2")
                .positionName("position2")
                .build();

        Mockito.when(negotiationService.getNegotiations(any(), any(), any()))
                .thenReturn(java.util.List.of(dto1, dto2));

        mockMvc.perform(get("/v1/negotiation")
                        .param("from", "0")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user(authUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].state").value("invitation"))
                .andExpect(jsonPath("$[0].apiProvider").value(ApiProvider.HEADHUNTER.name()))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].state").value("invitation"))
                .andExpect(jsonPath("$[1].apiProvider").value(ApiProvider.SUPERJOB.name()));
    }

    @Test
    @WithMockUser
    void updateNegotiation_validationError_idNull() throws Exception {
        NegotiationRequestDto request = new NegotiationRequestDto(
                null,
                NegotiationState.INVITATION,
                "comment"
        );

        mockMvc.perform(post("/v1/negotiation")
                        .with(SecurityMockMvcRequestPostProcessors.user(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    @WithMockUser
    void updateNegotiation_validationError_commentBlank() throws Exception {
        NegotiationRequestDto request = new NegotiationRequestDto(
                5,
                NegotiationState.INVITATION,
                ""
        );

        mockMvc.perform(post("/v1/negotiation")
                        .with(SecurityMockMvcRequestPostProcessors.user(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }

}
