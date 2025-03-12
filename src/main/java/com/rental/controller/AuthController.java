package com.rental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.rental.dto.AuthLoginDTO;
import com.rental.dto.AuthRegisterDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.dto.UserDTO;
import com.rental.service.AuthService;
import com.rental.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.logging.Logger;

/**
 * Contrôleur pour la gestion de l'authentification et des utilisateurs.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Endpoints pour l'inscription, la connexion et la récupération de l'utilisateur authentifié")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * Inscription d'un nouvel utilisateur.
     */
    @Operation(summary = "Enregistrer un nouvel utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur inscrit avec succès"),
            @ApiResponse(responseCode = "400", description = "Données d'inscription invalides")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRegisterDTO registerDTO) {
        logger.info("Tentative d'inscription pour : " + registerDTO.getEmail());
        AuthResponseDTO response = authService.register(registerDTO);
        logger.info("Inscription réussie pour : " + registerDTO.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Connexion d'un utilisateur.
     */
    @Operation(summary = "Connexion d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Échec de l'authentification")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthLoginDTO loginDTO) {
        logger.info("Tentative de connexion pour : " + loginDTO.getEmail());

        try {
            AuthResponseDTO response = authService.login(loginDTO);
            logger.info("Connexion réussie pour : " + loginDTO.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warning(
                    "Échec de l'authentification pour : " + loginDTO.getEmail() + " - Erreur : " + e.getMessage());
            return ResponseEntity.status(401).body(new AuthResponseDTO("Échec de l'authentification"));
        }
    }

    /**
     * Récupère les informations de l'utilisateur actuellement authentifié.
     */
    @Operation(summary = "Obtenir l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        logger.info("Récupération de l'utilisateur connecté : " + email);
        UserDTO userDTO = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDTO);
    }

}
