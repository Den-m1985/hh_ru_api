package com.example.controller.interfaces;

import com.example.dto.agregator_dto.ExperienceGradeRequest;
import com.example.dto.agregator_dto.ExperienceGradeResponse;
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

@Tag(name = "Experience Grade API", description = "методы для управления опытом")
public interface ExperienceGradeApi {

    @Operation(
            summary = "Получить грейд по ID",
            description = "Возвращает информацию о грейде опыта (Junior, Middle, Senior и т.д.) по его ID."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = ExperienceGradeResponse.class))
    )
    ResponseEntity<ExperienceGradeResponse> getGradeById(@PathVariable Integer id);


    @Operation(
            summary = "Получить грейд по названию",
            description = "Возвращает информацию о грейде по его имени (например, Junior, Middle, Senior)."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = ExperienceGradeResponse.class))
    )
    ResponseEntity<ExperienceGradeResponse> getGradeByName(@PathVariable String name);


    @Operation(
            summary = "Добавить новый грейд опыта",
            description = "Создаёт новый грейд (например, Junior, Middle, Senior) с описанием."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = ExperienceGradeResponse.class))
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные для создания нового грейда",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "Junior",
                                    summary = "Начальный уровень",
                                    description = "Пример запроса для добавления грейда Junior",
                                    value = """
                                            {
                                              "name": "Junior",
                                              "description": "Начальный уровень, базовые знания"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "Senior",
                                    summary = "Продвинутый уровень",
                                    description = "Пример запроса для добавления грейда Senior",
                                    value = """
                                            {
                                              "name": "Senior",
                                              "description": "Продвинутый уровень, глубокие знания и лидерство"
                                            }
                                            """
                            )
                    }
            )
    )
    ResponseEntity<ExperienceGradeResponse> addGrade(@RequestBody ExperienceGradeRequest request);


    @Operation(
            summary = "Получить все грейды опыта",
            description = "Возвращает список всех грейдов (Junior, Middle, Senior и т.д.)."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = ExperienceGradeResponse.class))
    )
    ResponseEntity<List<ExperienceGradeResponse>> getAllGrades();


    @Operation(summary = "Удалить грейд опыта")
    ResponseEntity<Void> deleteGrade(@PathVariable Integer id);
}
