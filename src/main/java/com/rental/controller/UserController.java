package com.rental.controller;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rental.dto.UserDTO;
import com.rental.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "Gestion des utilisateurs")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Récupère un utilisateur par ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non autorisé")
    })
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            logger.warning("Requête avec un ID utilisateur invalide : " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Invalid ID -> Bad Request
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warning("Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        logger.info("Requête pour récupérer un utilisateur avec l'ID : " + id);

        try {
            UserDTO userDTO = userService.findUserById(id);
            if (userDTO == null) {
                logger.warning("Utilisateur non trouvé : " + id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // Unauthorized if user is not found
            }
            return ResponseEntity.ok(userDTO);  // Return user if found -> 200 OK
        } catch (Exception e) {
            logger.warning("Erreur lors de la récupération de l'utilisateur avec l'ID : " + id + " | Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // Unauthorized in case of error (authentication issue)
        }
    }
}
