package com.rental.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.RentalResponse;
import com.rental.dto.UpdateRentalDTO;
import com.rental.service.AuthService;
import com.rental.service.RentalService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    public ResponseEntity<RentalResponse> createRental(
            @ModelAttribute CreateRentalDTO createRentalDTO,
            @RequestParam(value = "image", required = false) MultipartFile picture) {

        logger.info("Données reçues pour la création d'une location : " + createRentalDTO);
        logger.info("🔹 Requête reçue pour créer une location.");

        // Vérifier si l'image est présente dans la requête
        if (picture != null && !picture.isEmpty()) {
            logger.info("📷 Image reçue : " + picture.getOriginalFilename() +
                    " | Taille : " + picture.getSize() + " octets | Type : " + picture.getContentType());
        } else {
            logger.warning("Aucune image n'a été fournie.");
        }

        // Récupérer l'ID de l'utilisateur authentifié
        Long ownerId = authService.getAuthenticatedUserId();
        logger.info("Utilisateur authentifié avec ID : " + ownerId);

        // Appeler le service pour créer la location
        RentalDTO rentalDTO = rentalService.createRental(createRentalDTO, picture, ownerId);
        logger.info("Location créée avec succès : " + rentalDTO);

        // Créer la réponse
        RentalResponse rentalResponse = new RentalResponse("Location créée avec succès", rentalDTO);

        // Retourner la réponse
        return new ResponseEntity<>(rentalResponse, HttpStatus.CREATED);
    }

    /**
     * Endpoint pour mettre à jour une location existante.
     *
     * @param id Identifiant de la location à mettre à jour.
     * @param updateRentalDTO Données mises à jour pour la location.
     * @return DTO de la location mise à jour.
     */
    @Operation(summary = "Mettre à jour une location", description = "Permet de mettre à jour les informations d'une location.")
    @ApiResponse(responseCode = "200", description = "Location mise à jour avec succès.")
    @ApiResponse(responseCode = "401", description = "Non autorisée.")
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<RentalResponse> updateRental(
            @PathVariable Long id,
            @ModelAttribute UpdateRentalDTO updateRentalDTO) {

        logger.info("Données reçues pour la mise à jour de la location avec ID : " + id);

        // Appeler le service pour mettre à jour la location
        RentalDTO updatedRentalDTO = rentalService.updateRental(id, updateRentalDTO);

        if (updatedRentalDTO != null) {
            logger.info("Location mise à jour avec succès : " + updatedRentalDTO);
            RentalResponse rentalResponse = new RentalResponse("Location mise à jour avec succès", updatedRentalDTO);
            return ResponseEntity.ok(rentalResponse);
        } else {
            logger.warning("La location avec ID " + id + " n'a pas été trouvée pour mise à jour.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}