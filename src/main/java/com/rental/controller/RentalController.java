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

import java.util.Base64;
import java.util.List;
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
    public List<RentalDTO> getAllRentals() {
        logger.info("Début de getAllRentals : récupération de toutes les locations.");
        List<RentalDTO> rentals = rentalService.getAllRentals();
        logger.info("Fin de getAllRentals : locations récupérées avec succès.");
        return rentals;
    }

    @Operation(summary = "Récupère une location par ID")
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        logger.info("Début de getRentalById : récupération de la location avec ID " + id);
        try {
            RentalDTO rental = rentalService.getRental(id);
            logger.info("Fin de getRentalById : location trouvée pour ID " + id);
            return ResponseEntity.ok(rental);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la location avec ID " + id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Crée une nouvelle location")
    @PostMapping
    public ResponseEntity<RentalDTO> createRental(@RequestBody CreateRentalDTO createRentalDTO,
                                                  @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Début de createRental : création d'une nouvelle location.");

        if (image != null) {
            logger.info("Image reçue pour la location : " + image.getOriginalFilename());
        } else {
            logger.info("Aucune image reçue pour la location.");
        }

        // Decode the rental body with base64Url before processing
        String decodedDescription = new String(Base64.getUrlDecoder().decode(createRentalDTO.getDescription()));
        createRentalDTO.setDescription(decodedDescription);

        try {
            RentalDTO rental = rentalService.createRental(createRentalDTO, image);
            logger.info("Fin de createRental : location créée avec succès.");
            // Encode the rental body with base64Url before sending
            String encodedDescription = Base64.getUrlEncoder().encodeToString(rental.getDescription().getBytes());
            rental.setDescription(encodedDescription);
            return ResponseEntity.status(HttpStatus.CREATED).body(rental);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la création de la location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Met à jour une location existante")
    @PutMapping("/{id}")
    public ResponseEntity<RentalDTO> updateRental(@PathVariable Long id,
                                                  @RequestBody UpdateRentalDTO updateRentalDTO,
                                                  @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Début de updateRental : mise à jour de la location avec ID " + id);

        if (image != null) {
            logger.info("Nouvelle image reçue pour la mise à jour de la location : " + image.getOriginalFilename());
        } else {
            logger.info("Aucune nouvelle image reçue pour la mise à jour de la location.");
        }

        // Decode the rental body with base64Url before processing
        String decodedDescription = new String(Base64.getUrlDecoder().decode(updateRentalDTO.getDescription()));
        updateRentalDTO.setDescription(decodedDescription);

        try {
            RentalDTO rental = rentalService.updateRental(id, updateRentalDTO, image);
            logger.info("Fin de updateRental : location avec ID " + id + " mise à jour avec succès.");
            // Encode the rental body with base64Url before sending
            String encodedDescription = Base64.getUrlEncoder().encodeToString(rental.getDescription().getBytes());
            rental.setDescription(encodedDescription);
            return ResponseEntity.ok(rental);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la location avec ID " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}