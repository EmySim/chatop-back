package com.rental.controller;

import com.rental.dto.RentalDTO;
import com.rental.dto.CreateRentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for handling rental-related requests.
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private static final Logger logger = Logger.getLogger(RentalController.class.getName());
    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * Retrieves all rentals.
     *
     * @return ResponseEntity with the list of rentals.
     */
    @GetMapping
    public ResponseEntity<List<RentalDTO>> getAllRentals() {
        logger.info("Début de la méthode getAllRentals");

        try {
            List<RentalDTO> rentalDTOs = rentalService.getAllRentalDTOs();
            logger.info("Fin de la méthode getAllRentals");
            return ResponseEntity.ok(rentalDTOs);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des locations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Ajoute un message d'erreur ici si tu veux plus de détails
        }
    }

    /**
     * Retrieves details of a specific rental by ID.
     *
     * @param id The ID of the rental.
     * @return ResponseEntity with the rental details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        logger.info("Début de la méthode getRentalById");

        try {
            RentalDTO rentalDTO = rentalService.getRentalById(id);
            logger.info("Fin de la méthode getRentalById");
            return ResponseEntity.ok(rentalDTO);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Creates a new rental.
     *
     * @param createRentalDTO The DTO containing rental details.
     * @return ResponseEntity with the created rental.
     */
    @PostMapping
    public ResponseEntity<RentalDTO> createRental(@RequestBody CreateRentalDTO createRentalDTO) {
        logger.info("Début de la méthode createRental");

        try {
            RentalDTO rentalDTO = rentalService.createRental(createRentalDTO);
            logger.info("Fin de la méthode createRental");
            return ResponseEntity.status(HttpStatus.CREATED).body(rentalDTO);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la création de la location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Updates an existing rental.
     *
     * @param id The ID of the rental to update.
     * @param updateRentalDTO The DTO containing updated rental details.
     * @return ResponseEntity with the updated rental.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RentalDTO> updateRental(@PathVariable Long id, @RequestBody UpdateRentalDTO updateRentalDTO) {
        logger.info("Début de la méthode updateRental");

        try {
            RentalDTO rentalDTO = rentalService.updateRental(id, updateRentalDTO);
            logger.info("Fin de la méthode updateRental");
            return ResponseEntity.ok(rentalDTO);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
