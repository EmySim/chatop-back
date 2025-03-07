package com.rental.controller;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.service.AuthService;
import com.rental.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrôleur pour gérer les locations (rentals).
 */
@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "Endpoints pour gérer les locations")
public class RentalController {

    private static final Logger logger = Logger.getLogger(RentalController.class.getName());

    private RentalService rentalService;
    private AuthService authService;

    /**
     * Constructeur avec injection des services.
     *
     * @param rentalService Service de gestion des locations.
     * @param authService   Service de gestion de l'authentification.
     */

    @Autowired
    public RentalController(RentalService rentalService, AuthService authService) {
        this.rentalService = Objects.requireNonNull(rentalService, "RentalService ne peut pas être null");
        this.authService = Objects.requireNonNull(authService, "AuthService ne peut pas être null");
    }

    /**
     * Récupère toutes les locations.
     *
     * @return Liste de {@link RentalDTO}
     */

    @Operation(summary = "Récupérer toutes les locations", description = "Renvoie la liste de toutes les locations disponibles.")
    @ApiResponse(responseCode = "200", description = "Liste des locations récupérée avec succès", content = @Content(mediaType = "application/json"))
    @GetMapping
    public ResponseEntity<List<RentalDTO>> getAllRentals() {
        logger.info("Début de getAllRentals : récupération de toutes les locations.");
        List<RentalDTO> rentals = rentalService.getAllRentals();
        logger.info("Nombre de locations récupérées : " + rentals.size());
        return ResponseEntity.ok(rentals);
    }

    /**
     * Récupère une location par son ID.
     *
     * @param id Identifiant de la location.
     * @return La location trouvée ou une réponse 401 si non trouvée.
     */

    @Operation(summary = "Récupère une location par ID", parameters = @Parameter(name = "id", description = "ID de la location", required = true), responses = {
            @ApiResponse(responseCode = "200", description = "Location trouvée", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Location non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RentalDTO> getRentalById(
            @Parameter(description = "ID de la location à récupérer") @PathVariable Long id) {
        logger.info("Début de getRentalById : récupération de la location avec ID " + id);

        RentalDTO rental = rentalService.getRental(id);

        if (rental != null) {
            logger.info("Location trouvée : " + rental);
            return ResponseEntity.ok(rental);
        } else {
            logger.warning("Aucune location trouvée avec l'ID " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Crée une nouvelle location.
     *
     * @param createRentalDTO Données de la location à créer.
     * @param picture         (Optionnel) Image associée à la location.
     * @return La location créée.
     */

    @Operation(summary = "Crée une nouvelle location", responses = {
            @ApiResponse(responseCode = "200", description = "Location créée avec succès", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Données de création invalides")
    })

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<RentalDTO> createRental(
            @ModelAttribute CreateRentalDTO createRentalDTO,
            @RequestPart(value = "picture", required = true) MultipartFile picture) {

        try {
            logger.info("début createRental");

            Long authenticatedUserId = authService.getAuthenticatedUserId();
            createRentalDTO.setOwnerId(authenticatedUserId);

            // Stockage de l'image et mise à jour du DTO
            if (picture != null && !picture.isEmpty()) {
                String pictureURL = rentalService.saveImage(picture)
                        .orElseThrow(() -> new IllegalStateException("Impossible de sauvegarder l'image."));
                createRentalDTO.setPictureURL(pictureURL);
            }

            // Création de la location via RentalService
            RentalDTO rental = rentalService.createRental(createRentalDTO, picture, authenticatedUserId);
            logger.info("Location créée avec succès : " + rental);
            return ResponseEntity.status(HttpStatus.OK).body(rental);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la création de la location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Met à jour une location existante.
     *
     * @param id              ID de la location à mettre à jour.
     * @param updateRentalDTO Données mises à jour de la location.
     * @param picture         (Optionnel) Nouvelle image associée à la location.
     * @return La location mise à jour.
     */

    @Operation(summary = "Met à jour une location existante", parameters = @Parameter(name = "id", description = "ID de la location", required = true), responses = {
            @ApiResponse(responseCode = "200", description = "Location mise à jour avec succès", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour de la location")
    })
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<RentalDTO> updateRental(
            @PathVariable Long id,
            @ModelAttribute UpdateRentalDTO updateRentalDTO,
            @RequestPart(value = "picture", required = true) MultipartFile picture) {

        try {
            logger.info("Requête reçue pour mettre à jour la location avec ID " + id);

            Long authenticatedUserId = authService.getAuthenticatedUserId();
            logger.info("Utilisateur authentifié avec ID : " + authenticatedUserId);

            // Appeler le service métier pour mettre à jour la location
            RentalDTO rental = rentalService.updateRental(id, updateRentalDTO, picture);
            logger.info("Location mise à jour avec succès : " + rental);
            return ResponseEntity.ok(rental);
        } catch (SecurityException e) {
            logger.log(Level.WARNING, "Accès refusé pour la mise à jour de la location ID " + id, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la location avec ID " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
