package com.rental.controller;

import com.rental.dto.AuthRequestDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

/**
 * Contrôleur pour gérer les opérations liées à l'authentification.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint pour enregistrer un nouvel utilisateur.
     *
     * @param authRequest Les informations d'inscription.
     * @return Une réponse HTTP contenant un message de succès ou d'erreur.
     */
    @Operation(summary = "Enregistrer un nouvel utilisateur", description = "Inscrit un utilisateur avec email, mot de passe et nom.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur enregistré avec succès."),
            @ApiResponse(responseCode = "400", description = "Erreur de validation ou d'inscription.")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            logger.info("Requête reçue pour enregistrer un utilisateur : " + authRequest.getEmail());
            AuthResponseDTO response = authService.register(authRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warning("Erreur lors de l'inscription : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.severe("Erreur inattendue lors de l'inscription : " + e.getMessage());
            return ResponseEntity.badRequest().body("Une erreur est survenue lors de l'inscription.");
        }
    }
}