package com.rental.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ImageStorageService {

    private static final Logger logger = Logger.getLogger(ImageStorageService.class.getName());
    private final S3AsyncClient s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.endpoint-url}")
    private String s3EndpointUrl;

    public ImageStorageService(S3AsyncClient s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Stores an image to an S3 bucket asynchronously while streaming data directly.
     *
     * @param file The MultipartFile object to be uploaded.
     * @return A Mono wrapping the URL of the uploaded image.
     */
    public Mono<String> storeImage(MultipartFile file) {
        return Mono.fromCallable(() -> {
                    // Générer un nom de fichier unique pour éviter les conflits
                    String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

                    // Créer la requête S3 PutObject
                    PutObjectRequest request = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build();

                    // Télécharger le contenu du fichier sur S3
                    s3Client.putObject(
                            request,
                            AsyncRequestBody.fromBytes(file.getBytes())
                    ).join(); // Bloque jusqu'à la fin de l'opération

                    // Retourner l'URL complète du fichier stocké
                    return URI.create(s3EndpointUrl + "/" + bucketName + "/" + fileName).toString();
                })
                .subscribeOn(Schedulers.boundedElastic()) // Assurer une exécution non-bloquante dans un thread adapté
                .doOnError(error -> logger.log(Level.SEVERE, "Failed to upload image: " + error.getMessage(), error))
                .onErrorResume(error -> Mono.error(new RuntimeException("Image upload failed", error)));
    }

    /**
     * Deletes a file from the S3 bucket based on its URL.
     *
     * @param oldPictureUrl The full URL of the file to delete.
     */
    public void deleteFile(String oldPictureUrl) {
        try {
            // Extraire le nom du fichier de l'URL (après le dernier '/')
            String fileName = oldPictureUrl.substring(oldPictureUrl.lastIndexOf("/") + 1);

            // Créer la requête S3 DeleteObject
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            // Exécuter la suppression sur S3
            s3Client.deleteObject(request).join();

            logger.log(Level.INFO, "File deleted successfully: " + oldPictureUrl);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to delete file: " + e.getMessage(), e);
        }
    }

    /**
     * Stores a simple file (represented as a string) and returns its URL or an error message.
     *
     * @param picture The string content of the file to store.
     * @return The full S3 URL of the stored file.
     */
    public String storeFile(String picture) {
        try {
            // Générer un nom unique pour le fichier
            String fileName = UUID.randomUUID() + "-storedFile.txt";

            // Construire la requête S3 PutObject
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            // Convertir le contenu en bytes et stocker sur S3
            s3Client.putObject(
                    request,
                    AsyncRequestBody.fromBytes(picture.getBytes(StandardCharsets.UTF_8))
            ).join(); // Bloquer jusqu'à la fin du téléchargement

            // Retourner l'URL complète de l'objet stocké
            return s3EndpointUrl + "/" + bucketName + "/" + fileName;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to store file: " + e.getMessage(), e);
            return "Failed to store file";
        }
    }
}