package com.rental.controller;

import com.rental.dto.RentalDTO;
import com.rental.dto.CreateRentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.logging.Logger;

/**
 * Controller pour la gestion des locations.
 */
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
    public ResponseEntity<List<RentalDTO>> getAllRentals() {
        logger.info("Début de getAllRentals");
        List<RentalDTO> rentals = rentalService.getAllRentalDTOs();
        logger.info("Fin de getAllRentals");
        return ResponseEntity.ok(rentals);
    }

    @Operation(summary = "Récupère une location par ID")
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        logger.info("Début de getRentalById");
        RentalDTO rentalDTO = rentalService.getRentalById(id);
        return ResponseEntity.ok(rentalDTO);
    }

    @Operation(summary = "Crée une location avec image")
    @PostMapping(value = "/create-with-image", consumes = "multipart/form-data")
    public ResponseEntity<RentalDTO> createRentalWithImage(
            @RequestPart("rental") @Valid CreateRentalDTO rentalDTO,
            @RequestPart("image") MultipartFile image) {

        logger.info("Début de createRentalWithImage");
        RentalDTO newRental = rentalService.createRentalWithFile(rentalDTO, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRental);
    }

    @Operation(summary = "Met à jour une location")
    @PutMapping("/{id}")
    public ResponseEntity<RentalDTO> updateRental(@PathVariable Long id, @RequestBody UpdateRentalDTO updateRentalDTO) {
        logger.info("Début de updateRental");
        RentalDTO updatedRental = rentalService.updateRental(id, updateRentalDTO);
        return ResponseEntity.ok(updatedRental);
    }
}
