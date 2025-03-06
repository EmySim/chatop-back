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
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
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
}
