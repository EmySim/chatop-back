package com.rental.service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final Logger logger = Logger.getLogger(ImageStorageService.class.getName());

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
        logger.info("Début de l'upload de l'image vers S3.");
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String pictureURL = null;

        try {
            // Détecter le Content-Type basé sur l'extension
            String contentType = guessContentType(fileName);
            logger.info("Content-Type détecté : " + contentType);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType) // Ajouter le Content-Type ici
                    .build();

            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            if (response != null) {
                pictureURL = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
                logger.info("Upload terminé : " + pictureURL);
                return Optional.of(pictureURL);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'upload de l'image", e);
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
            return "application/octet-stream"; // Default fallback Content-Type
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
                return "application/octet-stream"; // Default Content-Type for unsupported extensions
        }
    }
}