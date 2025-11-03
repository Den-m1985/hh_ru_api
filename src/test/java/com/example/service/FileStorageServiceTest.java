package com.example.service;

import com.example.exceptions.FileStorageException;
import com.example.service.common.FileStorageService;
import com.example.util.ApplicationProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class FileStorageServiceTest {
    Path tempDir;
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("filestorage-test-");
        ApplicationProperties properties = new ApplicationProperties();
        properties.setStorageDir(tempDir.toString());
        fileStorageService = new FileStorageService(properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                        // Ignore cleanup errors
                    }
                });
    }

    // --- Tests for uploadFile ---
    @Test
    void shouldUploadFileSuccessfullyAndVerifyCreation() throws Exception {
        byte[] content = "test content".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "logo.png", "image/png", content
        );
        String resultFileName = fileStorageService.generateUniqueFileName(mockFile);
        fileStorageService.uploadFile(mockFile, resultFileName);
        Path savedFilePath = tempDir.resolve(resultFileName);

        assertThat(Files.exists(savedFilePath)).isTrue();
        assertThat(Files.readAllBytes(savedFilePath)).isEqualTo(content);
        assertThat(resultFileName).endsWith(".png");
        UUID.fromString(resultFileName.replace(".png", ""));
    }

    @Test
    void shouldUploadFileAndReturnUniqueName() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes());
        String fileName = fileStorageService.generateUniqueFileName(file);
        fileStorageService.uploadFile(file, fileName);

        assertNotNull(fileName);
        assertTrue(Files.exists(tempDir.resolve(fileName)));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.txt", "text/plain", new byte[0]);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            String fileName = fileStorageService.generateUniqueFileName(emptyFile);
            fileStorageService.uploadFile(emptyFile, fileName);
        });
        assertThat(exception.getMessage()).isEqualTo("File cannot be empty.");
    }

    // --- Tests for downloadFile ---
    @Test
    void shouldDownloadFileSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", "text/plain", "content".getBytes());
        String fileName = fileStorageService.generateUniqueFileName(file);
        fileStorageService.uploadFile(file, fileName);
        Resource resource = fileStorageService.downloadFile(fileName);

        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
        assertEquals("content", new String(resource.getInputStream().readAllBytes()));
    }

    @Test
    void shouldThrowFileNotFoundExceptionWhenResourceDoesNotExist() {
        assertThrows(FileStorageException.class, () -> {
            fileStorageService.downloadFile("notfound.txt");
        });
    }

    // --- Tests for deleteFile ---
    @Test
    void shouldCallFilesDeleteIfExistsSuccessfully() {
        String fileName = "file-to-delete.txt";
        Path expectedPath = tempDir.resolve(fileName).normalize();
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            fileStorageService.deleteFile(fileName);
            mockedFiles.verify(() -> Files.deleteIfExists(eq(expectedPath)), times(1));
        }
    }

}
