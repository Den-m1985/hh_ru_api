package com.example.controller.interfaces;

import com.example.dto.negotiation.NegotiationDto;
import com.example.dto.negotiation.NegotiationRequestDto;
import com.example.dto.negotiation.NegotiationStatistic;
import com.example.model.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(
        name = "HH and Superjob negotiations API",
        description = "Контроллер для получения откликов пользователя и статистики по откликам")
public interface NegotiationApi {

    @Operation(
            summary = "Получить статистику по откликам",
            description = "Возвращает статистику откликов по hh, superjob и общую статистику",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статистика откликов",
                            content = @Content(schema = @Schema(implementation = NegotiationStatistic.class))
                    )
             }
    )
    NegotiationStatistic getStatistic(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthUser authUser
    );

    //----------------------------------------

    @Operation(
            summary = "Получить отклики пользователя",
            description = "Возвращает список откликов пользователя с hh и superjob, отсортированные по времени отклика",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список откликов",
                            content = @Content(schema = @Schema(implementation = NegotiationDto[].class))
                    )
            }
    )
    List<NegotiationDto> getNegotiations(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size);

    //----------------------------------------

    @Operation(
            summary = "Обновить отклик",
            description = "Обновляет статус и/или комментарий к отклику",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отклик обновлен",
                            content = @Content(schema = @Schema(implementation = NegotiationDto.class))
                    )
            }
    )
    NegotiationDto updateNegotiation(@AuthenticationPrincipal AuthUser authUser,
                                     @RequestBody NegotiationRequestDto negotiationRequestDto);
}
