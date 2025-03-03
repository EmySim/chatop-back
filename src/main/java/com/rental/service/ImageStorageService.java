package com.rental.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final S3AsyncClient s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.endpoint-url}")
    private String s3EndpointUrl;

    public ImageStorageService(S3AsyncClient s3Client) {
        this.s3Client = s3Client;
    }

    public Mono<String> uploadImage(Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(filePart -> {
            String fileName = UUID.randomUUID() + "-" + filePart.filename();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            return filePart.content()
                    .reduce(new byte[0], (acc, buffer) -> {
                        byte[] newAcc = new byte[acc.length + buffer.remaining()];
                        System.arraycopy(acc, 0, newAcc, 0, acc.length);
                        buffer.get(newAcc, acc.length, buffer.remaining());
                        return newAcc;
                    })
                    .flatMap(bytes -> s3Client.putObject(request, AsyncRequestBody.fromBytes(bytes))
                            .thenReturn(URI.create(s3EndpointUrl + "/" + bucketName + "/" + fileName).toString()));
        });
    }
}


public class ImageStorageService {
}
