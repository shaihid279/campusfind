package com.campusfind.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${campusfind.upload.dir:uploads}")
    private String uploadDir;

    public String store(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) return null;

        try {
            Path dirPath = Paths.get(uploadDir, subfolder);
            Files.createDirectories(dirPath);

            String original = StringUtils.cleanPath(
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
            String extension = original.contains(".") ? original.substring(original.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID() + extension;

            Path targetPath = dirPath.resolve(filename);
            Files.copy(file.getInputStream(), targetPath);

            return "/uploads/" + subfolder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded file", e);
        }
    }
}