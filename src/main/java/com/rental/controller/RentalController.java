package com.rental.controller;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.service.AuthService;
import com.rental.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "Endpoints pour gérer les locations")
public class RentalController {

    private static final Logger logger = Logger.getLogger(RentalController.class.getName());

    private final RentalService rentalService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Autowired
    public RentalController(RentalService rentalService, AuthService authService) {
        this.rentalService = Objects.requireNonNull(rentalService, "RentalService ne peut pas être null");
        this.authService = Objects.requireNonNull(authService, "AuthService ne peut pas être null");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Operation(summary = "Récupérer toutes les locations")
    @ApiResponse(responseCode = "200", description = "Liste des locations récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<RentalDTO>> getAllRentals() {
        logger.info("Début de getAllRentals : récupération de toutes les locations.");
        List<RentalDTO> rentals = rentalService.getAllRentals();
        logger.info("Nombre de locations récupérées : " + rentals.size());
        return ResponseEntity.ok(rentals);
    }

    @Operation(summary = "Récupérer une location par ID")
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        logger.info("Début de getRentalById : récupération de la location avec ID " + id);
        RentalDTO rental = rentalService.getRental(id);
        return rental != null ? ResponseEntity.ok(rental) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<RentalDTO> createRental(
            @ModelAttribute CreateRentalDTO createRentalDTO,
            @RequestPart(value = "picture", required = false) MultipartFile picture) {

        logger.info("Début de createRental");
        Long ownerId = authService.getAuthenticatedUserId();
        logger.info("Utilisateur authentifié avec ID : " + ownerId);

        RentalDTO rentalDTO = rentalService.createRental(createRentalDTO, picture, ownerId);
        logger.info("Location créée avec succès : " + rentalDTO);

        return ResponseEntity.ok(rentalDTO);
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<RentalDTO> updateRental(
            @PathVariable Long id,
            @ModelAttribute UpdateRentalDTO updateRentalDTO,
            @RequestPart(value = "picture", required = false) MultipartFile picture) {

        try {
            logger.info("Mise à jour de la location avec ID " + id);
            Long authenticatedUserId = authService.getAuthenticatedUserId();
            logger.info("Utilisateur authentifié avec ID : " + authenticatedUserId);

            RentalDTO rental = rentalService.updateRental(id, updateRentalDTO, picture);
            logger.info("Location mise à jour avec succès : " + rental);
            return ResponseEntity.ok(rental);
        } catch (SecurityException e) {
            logger.log(Level.WARNING, "Accès refusé pour la mise à jour de la location", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
