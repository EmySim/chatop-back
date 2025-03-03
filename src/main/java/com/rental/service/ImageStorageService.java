package com.rental.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
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

    /**
     * Uploads an image to an S3 bucket asynchronously while streaming data directly.
     *
     * @param filePartMono A Mono wrapper containing the FilePart object to be uploaded.
     * @return A Mono wrapping the URL of the uploaded image.
     */
    public Mono<String> uploadImage(Mono<FilePart> filePartMono) {
        return filePartMono.flatMap(filePart -> {
            // Generate a unique filename for the image
            String fileName = UUID.randomUUID() + "-" + filePart.filename();

            // Create the PutObjectRequest for S3
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            // Use file content as InputStream directly for S3 upload
            return s3Client.putObject(
                            request,
                            AsyncRequestBody.fromPublisher(
                                    filePart.content()
                                            .map(dataBuffer -> {
                                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                                dataBuffer.read(bytes); // Read data chunk into byte array
                                                dataBuffer.release(); // Release DataBuffer reference
                                                return bytes;
                                            })
                            )
                    )
                    .map(response -> URI.create(s3EndpointUrl + "/" + bucketName + "/" + fileName).toString())
                    .doOnError(error -> {
                        // Logging or additional error handling (if needed)
                        System.err.println("Error during S3 upload: " + error.getMessage());
                    })
                    .onErrorResume(error -> Mono.error(new RuntimeException("Image upload failed", error)))
                    .subscribeOn(Schedulers.boundedElastic()); // Ensure non-blocking execution
        });
    }
}