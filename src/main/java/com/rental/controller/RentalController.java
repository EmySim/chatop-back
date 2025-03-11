package com.rental.controller;

import com.rental.dto.*;
import com.rental.service.AuthService;
import com.rental.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Contrôleur REST pour gérer les locations (Rentals).
 */
@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "Endpoints pour gérer les locations")
public class RentalController {

    private static final Logger logger = Logger.getLogger(RentalController.class.getName());

    private final RentalService rentalService;
    private final AuthService authService;

    public RentalController(RentalService rentalService, AuthService authService) {
        this.rentalService = rentalService;
        this.authService = authService;
    }

    @Operation(summary = "Récupérer toutes les locations")
    @ApiResponse(responseCode = "200", description = "Liste des locations récupérée avec succès.")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRentals() {
    logger.info("Récupération de toutes les locations.");
    
    // Récupération des locations en tant que liste de DTO
    List<RentalDTO> rentals = rentalService.getAllRentals();
    
    // Création de la réponse avec la clé "rentals"
    Map<String, Object> response = new HashMap<>();
    response.put("rentals", rentals);

    return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour récupérer la liste de toutes les locations.
     *
     * @return Liste des locations sous forme de DTO.
     */
    @Operation(summary = "Récupérer une location par ID")
    @ApiResponse(responseCode = "200", description = "Location récupérée avec succès.")
    @ApiResponse(responseCode = "404", description = "Location non trouvée.")
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
        logger.info("Récupération des détails de la location avec ID : " + id);
        RentalDTO rentalDTO = rentalService.getRental(id);
        if (rentalDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(rentalDTO);
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
            @RequestParam(value = "image", required = false) MultipartFile picture) {

        logger.info("Données reçues pour la création d'une location : " + createRentalDTO);
        logger.info("🔹 Requête reçue pour créer une location.");

        // Vérifier si l'image est présente dans la requête
        if (picture != null && !picture.isEmpty()) {
            logger.info("📷 Image reçue : " + picture.getOriginalFilename() +
                    " | Taille : " + picture.getSize() + " octets | Type : " + picture.getContentType());
        } else {
            logger.severe("🚨 ERREUR CRITIQUE : L'image est NULL ou vide !");
            throw new RuntimeException("L'image est obligatoire !");
        }

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
    @Operation(summary = "Mettre à jour une location")
    @ApiResponse(responseCode = "200", description = "Location mise à jour avec succès.")
    @ApiResponse(responseCode = "401", description = "Non autorisé.")
    @ApiResponse(responseCode = "404", description = "Location non trouvée.")
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<RentalDTO> updateRental(
            @PathVariable Long id,
            @ModelAttribute UpdateRentalDTO updateRentalDTO,
            @RequestPart(value = "picture", required = false) MultipartFile picture) {

        logger.info("Données reçues pour la mise à jour de la location avec ID : " + id);
        Long ownerId = authService.getAuthenticatedUserId();
        
        RentalDTO updatedRental = rentalService.updateRental(id, updateRentalDTO, ownerId);
        if (updatedRental == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(updatedRental);
    }
}