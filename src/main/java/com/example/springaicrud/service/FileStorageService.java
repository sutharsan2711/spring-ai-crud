package com.example.springaicrud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload.images-dir:src/main/resources/images}") String imagesDir) {
        this.uploadDir = resolveUploadDir(imagesDir);
    }

    public String storeImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Files.createDirectories(uploadDir);
        String original = Objects.toString(file.getOriginalFilename(), "");
        String cleaned = StringUtils.cleanPath(original);
        String ext = "";
        int dot = cleaned.lastIndexOf('.');
        if (dot >= 0 && dot < cleaned.length() - 1) {
            ext = cleaned.substring(dot);
        }
        String storedName = UUID.randomUUID() + ext;
        Path target = uploadDir.resolve(storedName).normalize();
        if (!target.startsWith(uploadDir)) {
            throw new IOException("Invalid file path");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/images/" + storedName;
    }

    public void deleteIfExists(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }
        String name = imagePath.replaceFirst("^/images/", "");
        Path target = uploadDir.resolve(name).normalize();
        if (!target.startsWith(uploadDir)) {
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
        }
    }

    private static Path resolveUploadDir(String configured) {
        Path p = Paths.get(configured);
        if (!p.isAbsolute()) {
            p = Paths.get(System.getProperty("user.dir")).resolve(p);
        }
        return p.normalize().toAbsolutePath();
    }
}

