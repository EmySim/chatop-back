package com.rental.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class ImageStorageService {

    // Remplacement de java.util.logging.Logger par SLF4J
    private static final Logger logger = LoggerFactory.getLogger(ImageStorageService.class.getName());
    private final S3AsyncClient s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.endpoint-url}")
    private String s3EndpointUrl;

    public ImageStorageService(S3AsyncClient s3Client) {
        this.s3Client = s3Client;
    }

    public Mono<String> storeImage(MultipartFile file) {
        return Mono.fromCallable(() -> {
                    String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                    PutObjectRequest request = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build();

                    s3Client.putObject(request, AsyncRequestBody.fromBytes(file.getBytes()))
                            .whenComplete((response, exception) -> {
                                if (exception != null) {
                                    logger.error("Image upload failed", exception);
                                } else {
                                    logger.info("Image uploaded successfully");
                                }
                            });

                    return URI.create(s3EndpointUrl + "/" + bucketName + "/" + fileName).toString();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> logger.error("Failed to upload image: " + error.getMessage(), error));
    }

    public void deleteFile(String oldPictureUrl) {
        String fileName = oldPictureUrl.substring(oldPictureUrl.lastIndexOf("/") + 1);
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(request).whenComplete((response, exception) -> {
            if (exception != null) {
                logger.error("Failed to delete file: " + exception.getMessage(), exception);
            } else {
                logger.info("File deleted successfully");
            }
        });
    }
}
