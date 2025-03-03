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
    private final ImageStorageService imageStorageService;

    public RentalController(RentalService rentalService, ImageStorageService imageStorageService) {
        this.rentalService = rentalService;
        this.imageStorageService = imageStorageService;
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

    @Operation(summary = "Met à jour une location")
    @PutMapping("/{id}")
    public ResponseEntity<RentalDTO> updateRental(@PathVariable Long id, @RequestBody UpdateRentalDTO updateRentalDTO) {
        logger.info("Début de updateRental");
        RentalDTO updatedRental = rentalService.updateRental(id, updateRentalDTO);
        return ResponseEntity.ok(updatedRental);
    }

    @Operation(summary = "Crée une location avec FormData")
    @PostMapping(value = "/create-with-formdata", consumes = "multipart/form-data")
    public ResponseEntity<RentalDTO> createRentalWithFormData(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("location") String location,
            @RequestParam("surface") int surface,
            @RequestParam("owner_id") Long ownerId,
            @RequestPart("image") MultipartFile image) {

        logger.info("Début de createRentalWithFormData");
        CreateRentalDTO rentalDTO = new CreateRentalDTO(name, description, price, location, surface, "", ownerId);
        String pictureUrl = imageStorageService.storeImage(image).block();
        rentalDTO.setPicture(pictureUrl);
        RentalDTO newRental = rentalService.createRental(rentalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRental);
    }

    @Operation(summary = "Met à jour une location via POST")
    @PostMapping("/{rental_id}")
    public ResponseEntity<RentalDTO> updateRentalViaPost(@PathVariable("rental_id") Long rentalId, @RequestBody UpdateRentalDTO updateRentalDTO) {
        logger.info("Début de updateRentalViaPost");
        RentalDTO updatedRental = rentalService.updateRental(rentalId, updateRentalDTO);
        return ResponseEntity.ok(updatedRental);
    }
}
