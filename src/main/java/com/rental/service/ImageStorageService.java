package com.rental.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.async.AsyncRequestBody;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ImageStorageService {

    private static final Logger logger = Logger.getLogger(ImageStorageService.class.getName());
    private final S3AsyncClient s3Client;
    private final String bucketName = "your-bucket-name"; // A déplacer dans une configuration

    public ImageStorageService(S3AsyncClient s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Méthode réactive pour stocker une image dans S3
     * @param file Le fichier image à stocker
     * @return Un Mono contenant une URL publique de l'image stockée
     */
    public Mono<String> storeImage(MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return Mono.fromCallable(() -> file.getBytes())
                .flatMap(bytes -> Mono.fromFuture(
                        s3Client.putObject(request, AsyncRequestBody.fromBytes(bytes))
                ))
                .doOnError(e -> logger.log(Level.SEVERE, "Erreur lors de la mise en ligne de l'image", e))
                .thenReturn("https://s3.amazonaws.com/" + bucketName + "/" + fileName);
    }

    public Optional<String> saveImage(MultipartFile image) {
    }
}