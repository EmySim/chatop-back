package com.rental.controller;

import com.rental.dto.RentalDTO;
import com.rental.dto.CreateRentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "Endpoints pour gérer les locations")
public class RentalController {

    private static final Logger logger = Logger.getLogger(RentalController.class.getName());
    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = Objects.requireNonNull(rentalService, "RentalService ne peut pas être null");
    }

    @Operation(summary = "Récupère toutes les locations")
    @GetMapping
    public Flux<RentalDTO> getAllRentals() {
        logger.info("Début de getAllRentals : récupération de toutes les locations.");
        return rentalService.getAllRentals()
                .doOnComplete(() -> logger.info("Fin de getAllRentals : locations récupérées avec succès."));
    }

    @Operation(summary = "Récupère une location par ID")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RentalDTO>> getRentalById(@PathVariable Long id) {
        logger.info("Début de getRentalById : récupération de la location avec ID " + id);
        return rentalService.getRental(id)
                .map(ResponseEntity::ok)
                .doOnSuccess(rental -> {
                    if (rental != null) {
                        logger.info("Fin de getRentalById : location trouvée pour ID " + id);
                    } else {
                        logger.warning("Fin de getRentalById : aucune location trouvée pour ID " + id);
                    }
                })
                .onErrorResume(e -> {
                    logger.log(Level.SEVERE, "Erreur lors de la récupération de la location avec ID " + id, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
                });
    }

    @Operation(summary = "Crée une nouvelle location")
    @PostMapping
    public Mono<ResponseEntity<RentalDTO>> createRental(@RequestBody CreateRentalDTO createRentalDTO,
                                                        @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Début de createRental : création d'une nouvelle location.");

        if (image != null) {
            logger.info("Image reçue pour la location : " + image.getOriginalFilename());
        } else {
            logger.info("Aucune image reçue pour la location.");
        }

        return rentalService.createRental(createRentalDTO, image)
                .map(rental -> {
                    logger.info("Fin de createRental : location créée avec succès.");
                    return ResponseEntity.status(HttpStatus.CREATED).body(rental);
                })
                .onErrorResume(e -> {
                    logger.log(Level.SEVERE, "Erreur lors de la création de la location", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }

    @Operation(summary = "Met à jour une location existante")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RentalDTO>> updateRental(@PathVariable Long id,
                                                        @RequestBody UpdateRentalDTO updateRentalDTO,
                                                        @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Début de updateRental : mise à jour de la location avec ID " + id);

        if (image != null) {
            logger.info("Nouvelle image reçue pour la mise à jour de la location : " + image.getOriginalFilename());
        } else {
            logger.info("Aucune nouvelle image reçue pour la mise à jour de la location.");
        }

        return rentalService.updateRental(id, updateRentalDTO, image)
                .map(rental -> {
                    logger.info("Fin de updateRental : location avec ID " + id + " mise à jour avec succès.");
                    return ResponseEntity.ok(rental);
                })
                .onErrorResume(e -> {
                    logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la location avec ID " + id, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }
}
