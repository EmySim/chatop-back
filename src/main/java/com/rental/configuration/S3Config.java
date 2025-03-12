package com.rental.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    // Injection des valeurs des propriétés définies dans le fichier de configuration
    @Value("${aws.s3.access-key}")
    private String accessKeyId;

    @Value("${aws.s3.secret-key}")
    private String secretAccessKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Crée et configure un client S3 avec les credentials et la région spécifiés.
     * Si les credentials ne sont pas définis, utilise le profil par défaut.
     *
     * @return S3Client configuré
     */
    @Bean
    public S3Client s3Client() {
        if (accessKeyId == null || accessKeyId.isBlank() || secretAccessKey == null || secretAccessKey.isBlank()) {
            // Utilisation du profil par défaut si les credentials ne sont pas définis
            return S3Client.builder()
                    .region(Region.of(region))
                    .build();
        }

        // Configuration du client S3 avec les credentials spécifiés
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
    }

    /**
     * Retourne le nom du bucket S3 configuré.
     *
     * @return Nom du bucket S3
     */
    @Bean
    public String bucketName() {
        return bucketName;
    }
}