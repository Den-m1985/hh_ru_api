package com.example.controller;

import com.example.controller.interfaces.FileControllerApi;
import com.example.service.common.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
public class FileController implements FileControllerApi {
    private final FileStorageService fileStorageService;

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String fileName, @RequestParam(defaultValue = "false") boolean download
    ) {
        Resource resource = fileStorageService.downloadFile(fileName);
        String contentType = fileStorageService.determineContentType(resource);
        String disposition = download ? "attachment" : "inline";
        String headerValue = String.format("%s; filename=\"%s\"", disposition, resource.getFilename());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}
