package com.example.controller.interfaces;

import com.example.dto.company.CompanyResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Companies API", description = "Управление компаниями и поиск компаний по фильтрам")
public interface CompanyApi {

    @Operation(
            summary = "Получить компанию по ID",
            description = "Возвращает данные о компании по её уникальному идентификатору."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyResponseDto.class))
    )
    ResponseEntity<CompanyResponseDto> getCompanyById(@PathVariable Integer id);


    @Operation(
            summary = "Добавить компанию без логотипа",
            description = "Создаёт новую компанию на основе переданных данных."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyResponseDto.class))
    )
    ResponseEntity<CompanyResponseDto> addCompany(@RequestBody CompanyResponseDto response);


    @Operation(
            summary = "Добавить компанию с логотипом",
            description = "Создаёт новую компанию на основе переданных данных."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyResponseDto.class))
    )
    ResponseEntity<CompanyResponseDto> addCompany(
            @RequestPart("companyData") CompanyResponseDto response,
            @RequestPart(value = "logoFile", required = false) MultipartFile logoFile
    );


    @Operation(
            summary = "Добавить логотип в существующюю компанию",
            description = "Создаёт новую компанию на основе переданных данных."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyResponseDto.class))
    )
    ResponseEntity<CompanyResponseDto> uploadLogo(
            @PathVariable Integer companyId,
            @RequestPart("File") MultipartFile file
    );


    @Operation(
            summary = "Получить список всех компаний",
            description = "Возвращает список всех компаний, сохранённых в системе."
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = CompanyResponseDto.class))
    )
    ResponseEntity<List<CompanyResponseDto>> getAllCompanies();


    @Operation(
            summary = "Фильтр компаний по категориям",
            description = "Возвращает список компаний, отфильтрованных по выбранным категориям. " +
                    "Если выбрать несколько категорий, то в выдаче будут компании всех этих категорий."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список компаний по категориям",
            content = @Content(schema = @Schema(implementation = CompanyResponseDto.class))
    )
    ResponseEntity<List<CompanyResponseDto>> getCompaniesByFilters(@RequestParam List<String> categories);


    @Operation(summary = "Удалить компанию")
    ResponseEntity<Void> deleteCompany(@PathVariable Integer id);
}
