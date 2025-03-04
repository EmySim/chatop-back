package com.rental.controller;

import com.rental.dto.RentalDTO;
import com.rental.dto.CreateRentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.service.RentalService;
import com.rental.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "Endpoints pour gérer les locations")
public class RentalController {

    private static final Logger logger = Logger.getLogger(RentalController.class.getName());
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Récupère toutes les locations")
    @GetMapping
    public Flux<RentalDTO> getAllRentals() {
        logger.info("Début de getAllRentals");
        return rentalService.getAllRentals()
                .doOnComplete(() -> logger.info("Fin de getAllRentals"));
    }

    @Operation(summary = "Récupère une location par ID")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RentalDTO>> getRentalById(@PathVariable Long id) {
        logger.info("Début de getRentalById");
        return rentalService.getRental(id)
                .map(ResponseEntity::ok)
                .doOnSuccess(rental -> logger.info("Fin de getRentalById"))
                .onErrorResume(e -> {
                    logger.warning("Erreur : " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
                });
    }

    @Operation(summary = "Crée une nouvelle location")
    @PostMapping
    public Mono<ResponseEntity<RentalDTO>> createRental(@RequestBody CreateRentalDTO createRentalDTO,
                                                        @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Début de createRental");
        return rentalService.createRental(createRentalDTO, image)
                .map(rental -> ResponseEntity.status(HttpStatus.CREATED).body(rental))
                .doOnSuccess(rental -> logger.info("Fin de createRental"))
                .onErrorResume(e -> {
                    logger.severe("Erreur lors de la création de la location : " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }

    @Operation(summary = "Met à jour une location existante")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RentalDTO>> updateRental(@PathVariable Long id,
                                                        @RequestBody UpdateRentalDTO updateRentalDTO,
                                                        @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Début de updateRental");
        return rentalService.updateRental(id, updateRentalDTO, image)
                .map(ResponseEntity::ok)
                .doOnSuccess(rental -> logger.info("Fin de updateRental"))
                .onErrorResume(e -> {
                    logger.severe("Erreur lors de la mise à jour de la location : " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }
}