package com.rental.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}") // Récupération du dossier défini dans application.properties
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        // Vérifier si le dossier de stockage existe, sinon le créer
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom unique pour éviter les conflits
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Sauvegarder le fichier sur le disque
        Files.copy(file.getInputStream(), filePath);

        // Retourner l'URL de l'image stockée
        return "/uploads/" + fileName;
    }

    public String storeFileWithFormData(MultipartFile file, String additionalData) throws IOException {
        // Vérifier si le dossier de stockage existe, sinon le créer
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom unique pour éviter les conflits
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Sauvegarder le fichier sur le disque
        Files.copy(file.getInputStream(), filePath);

        // Log additional data
        System.out.println("Additional Data: " + additionalData);

        // Retourner l'URL de l'image stockée
        return "/uploads/" + fileName;
    }
}
