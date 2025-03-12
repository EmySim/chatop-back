package com.rental.service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class ImageStorageService {

    private final S3Client s3Client;
    private final String bucketName = "chatop-bucket-2025";

    public ImageStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Enregistre une image dans S3 et retourne son URL.
     *
     * @param file Fichier à stocker.
     * @return URL de l'image stockée.
     */
    public Optional<String> saveImage(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String pictureURL = null;

        try {
            // Détecter le Content-Type basé sur l'extension
            String contentType = guessContentType(fileName);

            // Construire la requête de mise en ligne
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            // Envoyer la requête à S3
            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            if (response != null) {
                pictureURL = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
                return Optional.of(pictureURL);
            }
        } catch (IOException e) {
            // Erreur lors de l'upload de l'image
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Devine le type MIME (Content-Type) de l'image en fonction de son extension.
     *
     * @param fileName Nom du fichier.
     * @return Un Content-Type valide ou une valeur par défaut.
     */
    private String guessContentType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "application/octet-stream"; // Content-Type par défaut
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream"; // Content-Type par défaut pour les extensions non supportées
        }
    }
}