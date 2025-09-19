package com.example.controller.interfaces;

import com.example.dto.RecruiterDto;
import com.example.dto.RecruiterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Recruiters API", description = "Управление рекрутерами и получение информации о них")
public interface RecruiterApi {

    @Operation(
            summary = "Получить рекрутера по ID",
            description = "Возвращает данные о рекрутере по его уникальному идентификатору."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = RecruiterDto.class))
    )
    ResponseEntity<RecruiterDto> getRecruiterById(@PathVariable Integer id);


    @Operation(
            summary = "Получить всех рекрутеров",
            description = "Возвращает список всех рекрутеров в системе."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = RecruiterDto.class))
    )
    ResponseEntity<List<RecruiterDto>> getAllRecruiters();


    @Operation(
            summary = "Получить рекрутеров по компании",
            description = "Возвращает список рекрутеров, работающих в указанной компании."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = RecruiterDto.class))
    )
    ResponseEntity<List<RecruiterDto>> getAllRecruitersByCompany(@PathVariable Integer companyId);


    @Operation(
            summary = "Добавить нового рекрутера",
            description = "Создаёт нового рекрутера в системе."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = RecruiterDto.class))
    )
    ResponseEntity<RecruiterDto> addRecruiter(@RequestBody RecruiterRequest request);


    @Operation(summary = "Удалить рекрутера")
    ResponseEntity<Void> deleteRecruiter(@PathVariable Integer id);
}
