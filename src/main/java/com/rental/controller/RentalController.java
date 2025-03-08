package com.rental.controller;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.service.AuthService;
import com.rental.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.logging.Logger;

/**
 * Contrôleur pour gérer les endpoints REST liés aux locations.
 * Permet de récupérer, créer et mettre à jour des locations, ainsi que d'associer des images.
 */
@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "Endpoints pour gérer les locations")
public class RentalController {

    private static final Logger logger = Logger.getLogger(RentalController.class.getName());

    private final RentalService rentalService;
    private final AuthService authService;

    /**
     * Constructeur pour injecter les dépendances.
     *
     * @param rentalService Instance du service de gestion des locations.
     * @param authService Instance du service d'authentification.
     */
    @Autowired
    public RentalController(RentalService rentalService, AuthService authService) {
        this.rentalService = rentalService;
        this.authService = authService;
    }

    /**
     * Endpoint pour récupérer la liste de toutes les locations.
     *
     * @return Liste des locations sous forme de DTO.
     */
    @Operation(summary = "Récupérer toutes les locations", description = "Retourne la liste complète des locations disponibles.")
    @ApiResponse(responseCode = "200", description = "Liste des locations récupérée avec succès.")
    @GetMapping
    public ResponseEntity<List<RentalDTO>> getAllRentals() {
        logger.info("Récupération de toutes les locations.");
        List<RentalDTO> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals);
    }

    /**
     * Endpoint pour récupérer une location spécifique par son ID.
     *
     * @param id Identifiant de la location.
     * @return DTO contenant les détails de la location.
     */
    @Operation(summary = "Récupérer une location par ID", description = "Retourne les détails d'une location spécifique.")
    @ApiResponse(responseCode = "200", description = "Location récupérée avec succès.")
    @ApiResponse(responseCode = "404", description = "Location non trouvée.")
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        logger.info("Récupération des détails de la location avec ID : " + id);
        RentalDTO rentalDTO = rentalService.getRental(id);
        if (rentalDTO != null) {
            return ResponseEntity.ok(rentalDTO);
        } else {
            logger.warning("La location avec ID " + id + " n'a pas été trouvée.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint pour créer une nouvelle location.
     *
     * @param createRentalDTO Données pour créer la location.
     * @param picture Fichier image de la location (optionnel).
     * @return DTO de la location créée.
     */
    @Operation(summary = "Créer une nouvelle location", description = "Permet de créer une location et d'associer une image.")
    @ApiResponse(responseCode = "200", description = "Location créée avec succès.")
    @ApiResponse(responseCode = "400", description = "Mauvaises données fournies.")
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<RentalDTO> createRental(
            @ModelAttribute CreateRentalDTO createRentalDTO,
            @RequestPart(value = "picture", required = false) MultipartFile picture) {

        logger.info("Données reçues pour la création d'une location : " + createRentalDTO);

        // Récupérer l'ID de l'utilisateur authentifié
        Long ownerId = authService.getAuthenticatedUserId();
        logger.info("Utilisateur authentifié avec ID : " + ownerId);

        // Appeler le service pour créer la location
        RentalDTO rentalDTO = rentalService.createRental(createRentalDTO, picture, ownerId);
        logger.info("Location créée avec succès : " + rentalDTO);

        // Retourner la réponse
        return ResponseEntity.ok(rentalDTO);
    }

    /**
     * Endpoint pour mettre à jour une location existante.
     *
     * @param id Identifiant de la location à mettre à jour.
     * @param updateRentalDTO Données mises à jour pour la location.
     * @param picture Nouvelle image de la location (optionnel).
     * @return DTO de la location mise à jour.
     */
    @Operation(summary = "Mettre à jour une location", description = "Permet de modifier les détails d'une location existante.")
    @ApiResponse(responseCode = "200", description = "Location mise à jour avec succès.")
    @ApiResponse(responseCode = "404", description = "Location non trouvée.")
    @ApiResponse(responseCode = "400", description = "Mauvaises données fournies.")
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<RentalDTO> updateRental(
            @PathVariable Long id,
            @ModelAttribute UpdateRentalDTO updateRentalDTO,
            @RequestPart(value = "picture", required = false) MultipartFile picture) {

        logger.info("Données reçues pour la mise à jour de la location avec ID : " + id);

        // Appeler le service pour mettre à jour la location
        RentalDTO updatedRentalDTO = rentalService.updateRental(id, updateRentalDTO, picture);

        if (updatedRentalDTO != null) {
            logger.info("Location mise à jour avec succès : " + updatedRentalDTO);
            return ResponseEntity.ok(updatedRentalDTO);
        } else {
            logger.warning("La location avec ID " + id + " n'a pas été trouvée pour mise à jour.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}