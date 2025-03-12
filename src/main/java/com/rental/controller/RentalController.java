package com.rental.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.SnackbarNotif;
import com.rental.dto.UpdateRentalDTO;
import com.rental.service.AuthService;
import com.rental.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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

    /**
     * Endpoint pour récupérer toutes les locations.
     *
     * @return Liste des locations sous forme de DTO.
     */
    @Operation(summary = "Récupérer toutes les locations")
    @ApiResponse(responseCode = "200", description = "Liste des locations récupérée avec succès.")
    @ApiResponse(responseCode = "401", description = "Non autorisé.")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRentals() {
        // Récupération des locations en tant que liste de DTO
        List<RentalDTO> rentals = rentalService.getAllRentals();

        // Création de la réponse avec la clé "rentals"
        Map<String, Object> response = new HashMap<>();
        response.put("rentals", rentals);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour récupérer une location par ID.
     *
     * @param id Identifiant de la location à récupérer.
     * @return DTO de la location récupérée.
     */
    @Operation(summary = "Récupérer une location par ID")
    @ApiResponse(responseCode = "200", description = "Location récupérée avec succès.")
    @ApiResponse(responseCode = "401", description = "Non autorisé.")
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable Long id) {
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
     * @param picture         Fichier image de la location (optionnel).
     * @return DTO de la location créée encapsulé dans SnackbarNotif.
     */
    @Operation(summary = "Créer une nouvelle location", description = "Permet de créer une location et d'associer une image.")
    @ApiResponse(responseCode = "200", description = "Location créée avec succès.")
    @ApiResponse(responseCode = "401", description = "Non autorisé.")
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<SnackbarNotif> createRental(
            @ModelAttribute CreateRentalDTO createRentalDTO,
            @RequestParam(value = "picture", required = false) MultipartFile picture) {

        // Vérifier si l'image est présente dans la requête
        if (picture == null || picture.isEmpty()) {
            throw new RuntimeException("L'image est obligatoire !");
        }

        // Récupérer l'ID de l'utilisateur authentifié
        Long ownerId = authService.getAuthenticatedUserId();

        // Appeler le service pour créer la location
        RentalDTO rentalDTO = rentalService.createRental(createRentalDTO, picture, ownerId);

        // Retourner la réponse encapsulée dans SnackbarNotif
        return ResponseEntity.ok(new SnackbarNotif(rentalDTO, "Location créée avec succès!"));
    }

    /**
     * Endpoint pour mettre à jour une location existante.
     *
     * @param id              Identifiant de la location à mettre à jour.
     * @param updateRentalDTO Données mises à jour pour la location.
     * @param picture         Nouvelle image de la location (optionnel).
     * @return DTO de la location mise à jour encapsulé dans SnackbarNotif.
     */
    @Operation(summary = "Mettre à jour une location")
    @ApiResponse(responseCode = "200", description = "Location mise à jour avec succès.")
    @ApiResponse(responseCode = "401", description = "Non autorisé.")
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<SnackbarNotif> updateRental(
            @PathVariable Long id,
            @ModelAttribute UpdateRentalDTO updateRentalDTO,
            @RequestPart(value = "picture", required = false) MultipartFile picture) {

        // Récupérer l'ID de l'utilisateur authentifié
        Long ownerId = authService.getAuthenticatedUserId();

        // Appeler le service pour mettre à jour la location
        RentalDTO updatedRental = rentalService.updateRental(id, updateRentalDTO, ownerId);
        if (updatedRental == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Retourner la réponse encapsulée dans SnackbarNotif
        return ResponseEntity.ok(new SnackbarNotif(updatedRental, "Location mise à jour avec succès!"));
    }
}