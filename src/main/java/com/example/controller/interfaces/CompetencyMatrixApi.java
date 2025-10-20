package com.example.controller.interfaces;

import com.example.dto.agregator_dto.CompetencyMatrixRequest;
import com.example.dto.agregator_dto.CompetencyMatrixResponse;
import com.example.dto.it_map.CompetencyAreasResponse;
import com.example.dto.it_map.CompetencyMatrixFilterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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
            summary = "Получить матрицу компетенций по специализации и опыту",
            description = "Возвращает матрицу компетенций по названию специализации (например, Backend, Frontend, QA) и опыту (junior, middle)"
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompetencyAreasResponse.class))
    )
    ResponseEntity<List<CompetencyAreasResponse>> getFilteredCompetencyMatrix(
            @RequestBody @Valid CompetencyMatrixFilterRequest request);



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
                    schema = @Schema(implementation = CompetencyMatrixRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Frontend-разработчик",
                                    summary = "Пример для Frontend",
                                    description = "Пример запроса для создания матрицы компетенций Frontend",
                                    value = """
                                        {
                                          "id": null,
                                          "specialization": "Frontend",
                                          "competencies": [
                                            {
                                              "id": null,
                                              "area": "HTML & CSS",
                                              "competencies": [
                                                {
                                                  "id": null,
                                                  "name": "Семантика",
                                                  "experienceGradeId": 1,
                                                  "experienceGradeName": "JUNIOR",
                                                  "attribute": [
                                                    "<header>, <section>, <footer>"
                                                  ]
                                                }
                                              ]
                                            }
                                          ]
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
