package com.rental.controller;

import com.rental.dto.AuthRequestDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.dto.UserDTO;
import com.rental.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Contrôleur pour gérer les opérations liées à l'authentification.
 */
@Tag(name = "Auth", description = "Gestion de l'authentification et des utilisateurs")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Enregistre un nouvel utilisateur.
     *
     * @param authRequest Informations d'inscription.
     * @return Token JWT généré.
     */
    @Operation(summary = "Enregistrer un nouvel utilisateur", description = "Inscrit un utilisateur avec email, mot de passe et nom.")
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody(description = "Détails du nouvel utilisateur", required = true)
            AuthRequestDTO authRequest) {

        logger.info("Tentative d'enregistrement pour : " + authRequest.getEmail());

        try {
            AuthResponseDTO response = authService.register(authRequest);

            logger.info("Enregistrement réussi pour : " + authRequest.getEmail());
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            logger.warning("Erreur d'enregistrement pour : " + authRequest.getEmail() + " - " + e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Authentifie un utilisateur et génère un JWT.
     *
     * @param authRequest Informations de connexion.
     * @return Token JWT.
     */
    @Operation(summary = "Authentifier un utilisateur", description = "Connecte un utilisateur et génère un token JWT.")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody(description = "Détails de connexion", required = true)
            AuthRequestDTO authRequest) {

        logger.info("Tentative de connexion pour : " + authRequest.getEmail());

        try {
            AuthResponseDTO response = authService.login(authRequest);

            logger.info("Connexion réussie pour : " + authRequest.getEmail());
            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            logger.warning("Échec de la connexion pour : " + authRequest.getEmail() + " - " + e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Identifiants invalides.");
            return ResponseEntity.status(401).body(errorResponse); // Unauthorized
        }
    }

    /**
     * Récupère les informations de l'utilisateur connecté.
     *
     * @param authentication Contexte d'authentification.
     * @return Profil de l'utilisateur.
     */
    @Operation(
            summary = "Obtenir le profil de l'utilisateur connecté",
            description = "Retourne les détails de l'utilisateur connecté."
    )
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            logger.warning("Accès refusé : utilisateur non authentifié.");

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Accès refusé.");
            return ResponseEntity.status(401).body(errorResponse); // Unauthorized
        }

        logger.info("Récupération des infos pour : " + authentication.getName());

        try {
            UserDTO userDTO = authService.getUserDetails(authentication.getName());
            return ResponseEntity.ok(userDTO);

        } catch (Exception e) {
            logger.warning("Erreur lors de la récupération du profil pour : " + authentication.getName() + " - " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur interne lors de la récupération du profil.");
            return ResponseEntity.status(500).body(errorResponse); // Internal Server Error
        }
    }
}
