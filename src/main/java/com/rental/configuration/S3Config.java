package com.rental.configuration;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    private static final Logger logger = Logger.getLogger(S3Config.class.getName());

    @Value("${aws.s3.access-key}")
    private String accessKeyId;

    @Value("${aws.s3.secret-key}")
    private String secretAccessKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        // Logs des variables pour vérifier qu'elles sont bien lues
        logger.info("Configuration AWS S3 :");
        logger.info("Access Key ID: " + (accessKeyId != null ? "OK" : "NON DÉFINIE"));
        logger.info("Secret Access Key: " + (secretAccessKey != null ? "OK" : "NON DÉFINIE"));
        logger.info("Région: " + region);
        logger.info("Bucket Name: " + bucketName);

        if (region == null || region.isBlank()) {
            throw new IllegalStateException("La région AWS S3 n'est pas configurée !");
        }

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
