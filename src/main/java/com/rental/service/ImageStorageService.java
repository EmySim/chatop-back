package com.rental.service;

import java.io.IOException;
import java.util.Optional;
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

    public Optional<String> saveImage(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String imageUrl = null;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            if (response != null) {
                imageUrl = "https://s3.amazonaws.com/" + bucketName + "/" + fileName;
                return Optional.of(imageUrl);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise en ligne de l'image : ", e);
        }

        return Optional.empty();
    }
}
