package com.rental.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rental.dto.UserDTO;
import com.rental.repository.UserRepository;
import com.rental.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrôleur gérant les opérations liées aux utilisateurs.
 */
@Tag(name = "User", description = "Gestion des utilisateurs")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Constructeur pour injecter le service utilisateur.
     *
     * @param userService Service utilisé pour les opérations sur les utilisateurs.
     * @param userRepository Repository utilisé pour les opérations sur les utilisateurs.
     */
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Récupérer un utilisateur par ID.
     *
     * @param id L'ID de l'utilisateur à récupérer.
     * @return ResponseEntity contenant l'utilisateur récupéré ou une erreur appropriée.
     */
    @Operation(summary = "Récupérer un utilisateur par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non autorisé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserByID(@PathVariable("id") Long id) {
        // Valider l'ID
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'ID utilisateur doit être un entier valide et strictement positif.");
        }

        // Récupérer l'ID de l'utilisateur authentifié
        Long authenticatedUserId;
        try {
            authenticatedUserId = userService.getAuthenticatedUserId();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Récupération de l'utilisateur authentifié échouée.");
        }

        // Vérifier si l'utilisateur connecté peut accéder à cet ID
        if (!authenticatedUserId.equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès interdit à cet utilisateur.");
        }

        // Récupérer les informations de l'utilisateur (DTO)
        UserDTO userDTO;
        try {
            userDTO = userService.getUserById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Erreur lors de la récupération de l'utilisateur par ID : " + id);
        }

        // Retourner la réponse avec succès
        return ResponseEntity.ok(userDTO);
    }
}