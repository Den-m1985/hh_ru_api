package com.example.controller.interfaces;

import com.example.dto.vacancy_dto.ResumeItemDto;
import com.example.model.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(
        name = "HeadHunter OAuth Integration",
        description = "Контроллер для работы с API HeadHunter через OAuth 2.0"
)
@SecurityRequirement(name = "bearerAuth")
public interface HeadHunterApi {


    @Operation(
            summary = "Получить URL для авторизации в HH",
            description = "Возвращает URL для перенаправления пользователя на страницу авторизации HH.ru",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "URL для авторизации",
                            content = @Content(schema = @Schema(example = "https://hh.ru/oauth/authorize?..."))
                    )}
    )
    ResponseEntity<String> getAuthUrl(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthUser authUser
    );


    @Operation(
            summary = "Получить список резюме пользователя",
            description = "Возвращает список резюме пользователя из HH.ru",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список резюме",
                            content = @Content(schema = @Schema(implementation = ResumeItemDto[].class))
                    )}
    )
    ResponseEntity<List<ResumeItemDto>> getMineResume(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthUser authUser
    );


    @Operation(
            summary = "Проверить валидность токена",
            description = "Проверяет, действителен ли токен доступа для работы с API HH.ru",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус токена",
                            content = @Content(schema = @Schema(example = "true")))
            }
    )
    ResponseEntity<Boolean> isTokenGood(
            @Parameter(hidden = true)
            @AuthenticationPrincipal AuthUser authUser
    );


    @Operation(
            summary = "Обновить токены доступа",
            description = "Обновляет access и refresh токены для работы с API HH.ru",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Результат обновления",
                            content = @Content(schema = @Schema(example = "true")))
            }
    )
    ResponseEntity<Boolean> refreshTokens();
}
