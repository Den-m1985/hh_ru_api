package com.example.service.common;

import com.example.exceptions.FileStorageException;
import com.example.util.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    private final Path storageDir;

    public FileStorageService(ApplicationProperties properties) {
        this.storageDir = Paths.get(properties.getStorageDir());
        try {
            if (!Files.exists(this.storageDir)) {
                Files.createDirectories(this.storageDir);
            }
        } catch (IOException e) {
            throw new FileStorageException("Failed to create storage directory: " + this.storageDir, e);
        }
    }

    public void uploadFile(MultipartFile file, String fileName) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty.");
        }
        Path filePath = storageDir.resolve(fileName);
        upload(file, filePath);
    }

    public String generateUniqueFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString();
        return uniqueFilename + extension;
    }

    private void upload(MultipartFile file, Path filePath) throws IOException {
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public Resource downloadFile(String fileName) throws FileNotFoundException {
        try {
            Path filePath = this.storageDir.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found for name: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File path is malformed: " + fileName);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.storageDir.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            String message = "Could not delete file" + e.getMessage();
            throw new FileStorageException(message, e);
        }
        log.info("File was deleted with name: {}", fileName);
    }

}
