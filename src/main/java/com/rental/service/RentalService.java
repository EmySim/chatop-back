package com.rental.service;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.repository.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
public class RentalService {

    // Remplacement de java.util.logging.Logger par SLF4J
    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);
    private final RentalRepository rentalRepository;
    private final ImageStorageService imageStorageService;

    public RentalService(RentalRepository rentalRepository, ImageStorageService imageStorageService) {
        this.rentalRepository = rentalRepository;
        this.imageStorageService = imageStorageService;
    }

    public Mono<String> createRental(CreateRentalDTO rentalDTO, MultipartFile image) {
        return imageStorageService.storeImage(image)
                .flatMap(pictureUrl -> {
                    Rental rental = new Rental();
                    rental.setName(rentalDTO.getName());
                    rental.setDescription(rentalDTO.getDescription());
                    rental.setPrice(rentalDTO.getPrice());
                    rental.setLocation(rentalDTO.getLocation());
                    rental.setSurface(rentalDTO.getSurface());
                    rental.setPicture(pictureUrl);
                    rental.setOwner_id(rentalDTO.getOwner_id());
                    rental.setCreatedAt(new Date());
                    rental.setUpdatedAt(new Date());

                    return rentalRepository.save(rental)
                            .map(savedRental -> "Rental created!");
                })
                .doOnSuccess(message -> logger.info(message))
                .doOnError(e -> logger.error("Erreur lors de la création de la location", e));
    }

    public Mono<String> updateRental(Long id, UpdateRentalDTO updateRentalDTO, MultipartFile image) {
        return rentalRepository.findById(id)
                .flatMap(rental -> imageStorageService.storeImage(image)
                        .flatMap(newPictureUrl -> {
                            rental.setPicture(newPictureUrl);
                            rental.setName(updateRentalDTO.getName());
                            rental.setDescription(updateRentalDTO.getDescription());
                            rental.setPrice(updateRentalDTO.getPrice());
                            rental.setLocation(updateRentalDTO.getLocation());
                            rental.setSurface(updateRentalDTO.getSurface());
                            rental.setOwner_id(updateRentalDTO.getOwner_id());
                            rental.setUpdatedAt(new Date());

                            return rentalRepository.save(rental)
                                    .map(savedRental -> "Rental updated!");
                        }))
                .doOnError(e -> logger.error("Erreur lors de la mise à jour de la location", e))
                .onErrorResume(e -> Mono.just("Erreur lors de la mise à jour"));
    }

}
