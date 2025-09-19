package com.example.controller.interfaces;

import com.example.dto.agregator_dto.CompanyCategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Company Categories API", description = "Управление категориями компаний")
public interface CompanyCategoryApi {

    @Operation(
            summary = "Получить категорию по ID",
            description = "Возвращает категорию компании по её уникальному идентификатору."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyCategoryDto.class))
    )
    ResponseEntity<CompanyCategoryDto> getCompanyCategoryById(@PathVariable Integer id);


    @Operation(
            summary = "Получить категорию по имени",
            description = "Возвращает категорию компании по её названию."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyCategoryDto.class))
    )
    ResponseEntity<CompanyCategoryDto> getCompanyCategoryByName(@PathVariable String name);


    @Operation(
            summary = "Список всех категорий",
            description = "Возвращает список всех доступных категорий компаний."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyCategoryDto.class))
    )
    ResponseEntity<List<CompanyCategoryDto>> getAllCompanyCategories();


    @Operation(
            summary = "Добавить категорию компании",
            description = "Создаёт новую категорию компании."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyCategoryDto.class))
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные категории компании",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Fin-Tech",
                                    summary = "Финансовые технологии",
                                    description = "Пример категории для финтех компаний",
                                    value = """
                                            {
                                              "name": "Fin-Tech",
                                              "description": "Финансовые технологии и сервисы"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "BigTech",
                                    summary = "Крупные технологические компании",
                                    value = """
                                            {
                                              "name": "BigTech",
                                              "description": "Мировые лидеры ИТ-индустрии"
                                            }
                                            """
                            )
                    }
            )
    )
    ResponseEntity<CompanyCategoryDto> addCompanyCategory(@RequestBody CompanyCategoryDto response);


    @Operation(summary = "Удалить категорию компании")
    ResponseEntity<Void> deleteCompanyCategory(@PathVariable Integer id);
}
