package com.rental.controller;

import com.rental.dto.RentalDTO;
import com.rental.dto.CreateRentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.service.RentalService;
import com.rental.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Contrôleur pour gérer les opérations liées aux locations.
 */
@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "Endpoints pour gérer les locations")
public class RentalController {

    private static final Logger logger = Logger.getLogger(RentalController.class.getName());
    private final RentalService rentalService;
    private final UserService userService;

    /**
     * Constructeur avec injection des services.
     *
     * @param rentalService Service de gestion des locations.
     * @param userService Service de gestion des utilisateurs.
     */
    @Autowired
    public RentalController(RentalService rentalService, UserService userService) {
        this.rentalService = Objects.requireNonNull(rentalService, "RentalService ne peut pas être null");
        this.userService = Objects.requireNonNull(userService, "UserService ne peut pas être null");
    }

    /**
     * Récupère toutes les locations.
     *
     * @return Liste de {@link RentalDTO}
     */
    @Operation(
            summary = "Récupère toutes les locations",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des locations récupérée avec succès", content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping
    public ResponseEntity<List<RentalDTO>> getAllRentals() {
        logger.info("Début de getAllRentals : récupération de toutes les locations.");
        List<RentalDTO> rentals = rentalService.getAllRentals();
        logger.info("Fin de getAllRentals : locations récupérées avec succès.");
        return ResponseEntity.ok(rentals);
    }

    /**
     * Récupère une location par son ID.
     *
     * @param id Identifiant de la location.
     * @return La location trouvée ou une réponse 404 si non trouvée.
     */
    @Operation(
            summary = "Récupère une location par ID",
            parameters = @Parameter(name = "id", description = "ID de la location", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Location trouvée", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Location non trouvée")
            }
    )
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

    /**
     * Crée une nouvelle location.
     *
     * @param createRentalDTO Données de la location à créer.
     * @param picture (Optionnel) Image associée à la location.
     * @return La location créée.
     */
    @Operation(
            summary = "Crée une nouvelle location",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Location créée avec succès", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Données de création invalides")
            }
    )
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<RentalDTO> createRental(
            @ModelAttribute CreateRentalDTO createRentalDTO,
            @RequestPart(value = "picture", required = true) MultipartFile picture) {
        try {
            // Stockage de l'image et mise à jour du DTO
            if (picture != null && !picture.isEmpty()) {
                String pictureURL = rentalService.saveImage(picture)
                        .orElseThrow(() -> new IllegalStateException("Impossible de sauvegarder l'image."));
                createRentalDTO.setpictureURL(pictureURL);
            }

            // Création de la location via RentalService
            RentalDTO createdRental = rentalService.createRental(createRentalDTO, picture, createRentalDTO.getOwnerId());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);

        } catch (IllegalStateException e) {
            logger.log(Level.SEVERE, "Erreur lors de la création de la location : " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur inattendue lors de la création de la location : " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Met à jour une location existante.
     *
     * @param id ID de la location à mettre à jour.
     * @param updateRentalDTO Données mises à jour de la location.
     * @param image (Optionnel) Nouvelle image associée à la location.
     * @return La location mise à jour.
     */
    @Operation(
            summary = "Met à jour une location existante",
            parameters = @Parameter(name = "id", description = "ID de la location", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Location mise à jour avec succès", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour de la location")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<RentalDTO> updateRental(
            @PathVariable Long id,
            @RequestBody UpdateRentalDTO updateRentalDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        logger.info("Début de updateRental : mise à jour de la location avec ID " + id);

        if (image != null) {
            logger.info("Nouvelle image reçue pour la mise à jour de la location : " + image.getOriginalFilename());
        } else {
            logger.info("Aucune nouvelle image reçue pour la mise à jour de la location.");
        }

        try {
            RentalDTO rental = rentalService.updateRental(id, updateRentalDTO, image);
            logger.info("Fin de updateRental : location avec ID " + id + " mise à jour avec succès.");
            return ResponseEntity.ok(rental);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la location avec ID " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
