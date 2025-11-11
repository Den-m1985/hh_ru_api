package com.example.controller.interfaces;

import com.example.dto.VacancyHistoryDto;
import com.example.model.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "История вакансий", description = "Управление историей просмотров и действий пользователя с вакансиями.")
public interface VacancyHistoryApi {

    @Operation(
            summary = "Получить историю по провайдеру",
            description = "Возвращает список записей истории вакансий, отфильтрованных по указанному провайдеру (например, 'hh', 'sj')."
    )
    @Parameter(
            name = "provider",
            description = "Провайдер вакансий (имя в нижнем регистре).",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string", example = "hh")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос. Возвращает список записей истории.",
            content = @Content(schema = @Schema(implementation = VacancyHistoryDto.class))
    )
    ResponseEntity<List<VacancyHistoryDto>> getHistoryByProvider(@PathVariable String provider);

    // ------------------------------------------------------------------------------------------------------

    @Operation(
            summary = "Получить личную историю пользователя",
            description = "Возвращает полную историю действий с вакансиями для текущего аутентифицированного пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос. Возвращает список личных записей истории.",
            content = @Content(schema = @Schema(implementation = VacancyHistoryDto.class))
    )
    ResponseEntity<List<VacancyHistoryDto>> getHistoryByUser(@AuthenticationPrincipal AuthUser authUser);

    // ------------------------------------------------------------------------------------------------------

    @Operation(
            summary = "Удалить запись из истории",
            description = "Удаляет конкретную запись из истории вакансий по её ID."
    )
    @Parameter(
            name = "id",
            description = "Уникальный идентификатор записи истории.",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "integer", format = "int32")
    )
    ResponseEntity<Void> deleteHistory(@PathVariable Integer id);
}
