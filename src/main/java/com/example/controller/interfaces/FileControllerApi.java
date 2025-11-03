package com.example.controller.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface FileControllerApi {

    @Operation(
            summary = "Получить файл (логотип, документ) по имени",
            description = "Возвращает содержимое файла (изображение, PDF и т.д.) для отображения в браузере. " +
                    "Может быть принудительно скачан с помощью параметра `download=true`."
    )
    @Parameter(
            name = "fileName",
            description = "Уникальное имя файла (идентификатор), сохраненного на сервере (например, 'd8a1c7f4-logo.png').",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string")
    )
    @Parameter(
            name = "download",
            description = "Если установлено в 'true', заставляет браузер скачать файл (Content-Disposition: attachment). По умолчанию: false (inline).",
            required = false,
            in = ParameterIn.QUERY,
            schema = @Schema(type = "boolean", defaultValue = "false")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Файл успешно найден и возвращен.",
            content = @Content(
                    mediaType = "application/octet-stream"
                    // Альтернативные медиатипы, которые могут быть возвращены
                    /*
                    examples = {
                        @ExampleObject(mediaType = "image/png"),
                        @ExampleObject(mediaType = "image/jpeg"),
                        @ExampleObject(mediaType = "application/pdf")
                    }
                    */
            )
    )
    ResponseEntity<Resource> getFile(@PathVariable String fileName, @RequestParam(defaultValue = "false") boolean download);
}
