package com.rental.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;

import java.util.concurrent.CompletableFuture;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String region;

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public void testS3Connection(S3AsyncClient s3AsyncClient) {
        CompletableFuture<Void> future = s3AsyncClient.listBuckets(ListBucketsRequest.builder().build())
                .thenAccept(response -> response.buckets().forEach(bucket ->
                        System.out.println("Bucket trouvé : " + bucket.name())));

        future.join(); // Bloque jusqu'à la fin de la requête
    }
}