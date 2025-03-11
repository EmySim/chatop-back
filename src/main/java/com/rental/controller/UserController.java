package com.rental.controller;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rental.repository.UserRepository;
import com.rental.dto.UserDTO;
import com.rental.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.server.ResponseStatusException;

/**
 * Contrôleur gérant les opérations liées aux utilisateurs.
 */
@Tag(name = "User", description = "Gestion des utilisateurs")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Constructeur pour injecter le service utilisateur.
     *
     * @param userService Service utilisé pour les opérations sur les utilisateurs.
     */
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Récupérer un utilisateur par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non autorisé"),
            @ApiResponse(responseCode = "403", description = "Accès interdit à cet utilisateur"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable ("id") Long id) {
        logger.info("Requête reçue pour récupérer l'utilisateur avec l'identifiant : " + id);

        // Vérification si l'ID est null
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant utilisateur doit être un entier valide et non nul.");
        }



        // Récupérer l'ID de l'utilisateur actuellement authentifié
        Long authenticatedUserId = userService.getAuthenticatedUserId();
        logger.info("Utilisateur authentifié avec l'ID : " + authenticatedUserId);

        // Vérifier si l'utilisateur connecté peut accéder à ces informations
        if (!authenticatedUserId.equals(id)) {
            logger.warning("Accès interdit. L'utilisateur connecté tente d'accéder à un ID qu'il ne possède pas.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès interdit à cet utilisateur");
        }

        // Récupérer le DTO de l'utilisateur
        UserDTO userDTO = userService.getUserById(id);
        logger.info("Utilisateur récupéré avec succès : " + userDTO);

        return ResponseEntity.ok(userDTO);
    }
}
