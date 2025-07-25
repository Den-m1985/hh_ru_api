package com.example.controller.interfaces;

import com.example.dto.VacancyRequest;
import com.example.dto.vacancy_dto.Area;
import com.example.dto.vacancy_dto.VacancyItem;
import com.example.model.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@Tag(name = "HeadHunter Vacancies API", description = "Операции для поиска и отклика на вакансии через hh.ru")
public interface HhVacancyAPI {

    @Operation(
            summary = "Получить все вакансии",
            description = "Возвращает список всех вакансий, соответствующих фильтрам поиска из запроса",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список вакансий успешно получен",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VacancyItem.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации или параметров запроса",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Неавторизован или пользователь не найден",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён (неверный токен, заблокированный аккаунт)",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(mediaType = "application/json"))
            }
    )
    Set<VacancyItem> getAllVacancies(@RequestBody VacancyRequest request);


    @Operation(
            summary = "Получить отфильтрованные вакансии",
            description = "Возвращает только подходящие вакансии после фильтрации (например, без откликов, по ключевым словам и др.)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Отфильтрованные вакансии успешно получены",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Пользователь не найден или не авторизован",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён (например, истёкший JWT)",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(mediaType = "application/json"))
            }
    )
    List<VacancyItem> getAllFilteredVacancies(@RequestBody VacancyRequest request,
                                              @Parameter(hidden = true)
                                              @AuthenticationPrincipal AuthUser authUser);


    @Operation(
            summary = "Отклик на релевантные вакансии",
            description = "Отправляет отклики на вакансии, соответствующие заданным фильтрам и ключевым словам",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Отклики успешно отправлены"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса"),
                    @ApiResponse(responseCode = "401", description = "Неавторизован или пользователь не найден"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён (аккаунт заблокирован или неверный JWT)"),
                    @ApiResponse(responseCode = "409", description = "Пользователь уже существует (например, при повторной регистрации)"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    ResponseEntity<Void> applyToVacancies(@RequestBody VacancyRequest request,
                                          @Parameter(hidden = true)
                                          @AuthenticationPrincipal AuthUser authUser);

    @Operation(
            summary = "Дерево всех регионов",
            description = """
                            Возвращает древовидный список всех регионов.
                            Значения в справочнике могут поменяться в любой момент
                            https://api.hh.ru/openapi/redoc#tag/Obshie-spravochniki/operation/get-areas
                            WARN - возвращает json размером в 1Мб
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Дерево регионов",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Area.class)
                            )
                    )
            }
    )
    ResponseEntity<List<Area>> getAreas(
            @Parameter(description = "Аутентифицированный пользователь", required = true, hidden = true)
            @AuthenticationPrincipal AuthUser authUser);
}
