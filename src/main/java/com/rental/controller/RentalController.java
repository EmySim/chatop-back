package com.rental.controller;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    // Logger pour le contrôle des logs avec java.util.logging
    private static final Logger logger = Logger.getLogger(RentalController.class.getName());
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Crée une nouvelle location")
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Mono<String> createRental(@ModelAttribute CreateRentalDTO rentalDTO,
                                     @RequestPart(value = "image", required = false) MultipartFile image) {

        logger.info("Démarrage de la création de la location.");

        // Assurer que l'image soit stockée dans un service de stockage externe (ex: AWS S3)
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

                    // Sauvegarde du Rental dans la base de données et gestion des retours via Mono
                    return rentalRepository.save(rental)
                            .map(savedRental -> {
                                logger.info("Location créée avec succès.");
                                return "Rental created!";
                            });
                })
                .doOnSuccess(message -> logger.info(message))
                .doOnError(e -> logger.log(Level.SEVERE, "Erreur lors de la création de la location", e));
    }

    @Operation(summary = "Met à jour une location existante")
    @PutMapping(value = "/{rental_id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Mono<ResponseEntity<String>> updateRental(
            @PathVariable("rental_id") Long id,
            @ModelAttribute UpdateRentalDTO updateRentalDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        logger.info("Début de la mise à jour de la location avec ID : " + id);

        return rentalService.updateRental(id, updateRentalDTO, image)
                .doOnSuccess(result -> logger.info("Succès de la mise à jour de la location avec ID : " + id))
                .doOnError(e -> logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la location avec ID : " + id, e))
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne")));
    }
}
