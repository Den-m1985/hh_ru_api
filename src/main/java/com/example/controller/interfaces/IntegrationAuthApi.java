package com.example.controller.interfaces;

import com.example.dto.interfaces.BaseResumeDto;
import com.example.model.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "API Authentication", description = "Universal endpoints for various job APIs")
public interface IntegrationAuthApi {

    @Operation(
            summary = "Получить URL для авторизации",
            description = "Возвращает URL для перенаправления пользователя на страницу авторизации",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "URL для авторизации",
                            content = @Content(schema = @Schema(example = "https://hh.ru/oauth/authorize?..."))
                    )}
    )
    ResponseEntity<String> getAuthUrl(
            @Parameter(description = "Provider name (e.g., 'headhunter', 'superjob')")
            @PathVariable String provider,
            @AuthenticationPrincipal AuthUser authUser
    );


    @Operation(summary = "Get user's resumes",
            description = "Retrieves a list of resumes for the authenticated user from a specific provider.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved resumes",
            content = @Content(mediaType = "application/json",
                    // Using a schema for the list
                    schema = @Schema(implementation = List.class),
                    examples = {
                            @ExampleObject(name = "HeadHunter Response",
                                    summary = "Example response for HeadHunter provider",
                                    description = "Shows the structure of the `ResumeItemDto` response.",
                                    value = """
                                            [
                                                { "title": "Java Developer", "id": "123456789" },
                                                { "title": "Spring Boot Expert", "id": "987654321" }
                                            ]
                                            """),
                            @ExampleObject(name = "SuperJob Response",
                                    summary = "Example response for SuperJob provider",
                                    description = "Shows the structure of the `SuperjobResumeResponse` response.",
                                    value = """
                                            [
                                                { "title": "Software Engineer", "id": 123456 },
                                                { "title": "Backend Dev", "id": 654321 }
                                            ]
                                            """)
                    }
            )
    )
    ResponseEntity<List<BaseResumeDto>> getMineResume(
            @Parameter(description = "Provider name (e.g., 'headhunter', 'superjob')")
            @PathVariable String provider,
            @AuthenticationPrincipal AuthUser authUser
    );


    @Operation(
            summary = "Проверить валидность токена",
            description = "Проверяет, действителен ли токен доступа для работы с api",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус токена",
                            content = @Content(schema = @Schema(example = "true")))
            }
    )
    ResponseEntity<Boolean> isTokenGood(
            @Parameter(description = "Provider name (e.g., 'headhunter', 'superjob')")
            @PathVariable String provider,
            @AuthenticationPrincipal AuthUser authUser
    );
}
