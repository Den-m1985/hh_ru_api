package com.example.controller.interfaces;

import com.example.dto.agregator_dto.CompetencyMatrixRequest;
import com.example.dto.agregator_dto.CompetencyMatrixResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CompetencyMatrixApi {

    @Operation(
            summary = "Получить матрицу компетенций по ID",
            description = "Возвращает полную информацию о матрице компетенций по её уникальному идентификатору."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompetencyMatrixResponse.class))
    )
    ResponseEntity<CompetencyMatrixResponse> getCompetencyMatrixById(@PathVariable Integer id);


    @Operation(
            summary = "Получить матрицу компетенций по специализации",
            description = "Возвращает матрицу компетенций по названию специализации (например, Backend, Frontend, QA)."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompetencyMatrixResponse.class))
    )
    ResponseEntity<CompetencyMatrixResponse> getCompetencyBySpecialisation(@PathVariable String specialization);


    @Operation(
            summary = "Добавить новую матрицу компетенций",
            description = "Создаёт новую матрицу компетенций для указанной специализации."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompetencyMatrixResponse.class))
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Backend-разработчик",
                                    summary = "Пример для Backend",
                                    description = "Пример запроса для создания матрицы компетенций Backend",
                                    value = """
                                            {
                                              "specialization": "Java",
                                              "competencies": "https://link to competencies",
                                              "technical_questions": "https://link to technical questions"
                                            }
                                            """
                            )
                    }
            )
    )
    ResponseEntity<CompetencyMatrixResponse> addCompetencyMatrix(@RequestBody CompetencyMatrixRequest request);


    @Operation(
            summary = "Получить все матрицы компетенций",
            description = "Возвращает список всех матриц компетенций по всем специализациям."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompetencyMatrixResponse.class))
    )
    ResponseEntity<List<CompetencyMatrixResponse>> getAllCompetencyMatrix();


    @Operation(summary = "Удалить матрицу компетенций")
    ResponseEntity<Void> deleteCompetencyMatrix(@PathVariable Integer id);
}
