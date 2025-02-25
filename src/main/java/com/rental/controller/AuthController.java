
package com.rental.controller;

import com.rental.dto.AuthLoginDTO;
import com.rental.dto.AuthRegisterDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.dto.UserDTO;
import com.rental.service.AuthService;
import com.rental.service.JwtService;
import com.rental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * Contrôleur pour la gestion de l'authentification et des utilisateurs.
 * Permet l'inscription, la connexion, et la récupération des informations de l'utilisateur connecté.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthService authService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * Inscription d'un nouvel utilisateur.
     *
     * @param registerDTO Contient l'email, le mot de passe et le nom de l'utilisateur.
     * @return Un token JWT en cas de succès ou un message d'erreur.
     */
    @Operation(summary = "Enregistrer un nouvel utilisateur", description = "Permet à un utilisateur de s'inscrire avec son email, nom et mot de passe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur enregistré avec succès, token JWT renvoyé."),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données d'inscription.")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRegisterDTO registerDTO) {
        logger.info("Requête pour enregistrer un nouvel utilisateur : " + registerDTO.getEmail());
        AuthResponseDTO response = authService.register(registerDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Connexion d'un utilisateur.
     *
     * @param loginDTO Contient l'email et le mot de passe.
     * @return Un token JWT si l'authentification réussit.
     */
    @Operation(summary = "Connexion d'un utilisateur", description = "Permet à un utilisateur de se connecter et d'obtenir un token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie, token JWT renvoyé."),
            @ApiResponse(responseCode = "401", description = "Échec de l'authentification.")
    })

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthLoginDTO loginDTO) {
        logger.info("Requête de connexion reçue pour : " + loginDTO.
                getEmail());

        try {
            // Authentifier l'utilisateur via le gestionnaire d'authentification
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

            // Mettre à jour le contexte de sécurité avec les détails de l'utilisateur authentifié
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Générer un token JWT pour l'utilisateur authentifié
            String jwtToken = jwtService.generateToken((UserDetails) authentication.getPrincipal());

            logger.info("Connexion réussie pour : " + loginDTO.getEmail());
            return ResponseEntity.ok(new AuthResponseDTO(jwtToken));

        } catch (Exception e) {
            logger.warning("Échec de l'authentification pour : " + loginDTO.getEmail());
            return ResponseEntity.status(401).body(new AuthResponseDTO("Échec de l'authentification"));
        }
    }

    /**
     * Récupère les informations de l'utilisateur actuellement authentifié.
     *
     * @return Les informations de l'utilisateur sous forme de DTO.
     */
    @Operation(summary = "Obtenir l'utilisateur connecté", description = "Renvoie les informations de l'utilisateur actuellement authentifié.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur renvoyé avec succès."),
            @ApiResponse(responseCode = "401", description = "Non autorisé.")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        logger.info("Requête pour récupérer l'utilisateur actuellement connecté.");

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warning("Aucun utilisateur authentifié trouvé.");
            return ResponseEntity.status(401).body(null); // Retourne une réponse non autorisée
        }

        String email = authentication.getName();
        UserDTO userDTO = userService.findUserDTOByEmail(email);
        logger.info("Utilisateur actuellement connecté : " + email);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Déconnexion de l'utilisateur.
     *
     * @param authentication L'authentification de l'utilisateur.
     * @return Un message de succès ou d'erreur.
     */
    @Operation(summary = "Déconnexion de l'utilisateur", description = "Permet à un utilisateur de se déconnecter en invalidant son token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie."),
            @ApiResponse(responseCode = "401", description = "Non autorisé.")
    })
    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDTO> logout(Authentication authentication) {
        logger.info("Requête de déconnexion reçue.");

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warning("Aucun utilisateur authentifié trouvé.");
            return ResponseEntity.status(401).body(new AuthResponseDTO("Non autorisé"));
        }

        String email = authentication.getName();
        jwtService.invalidateToken(email);
        SecurityContextHolder.clearContext();
        logger.info("Déconnexion réussie pour : " + email);
        return ResponseEntity.ok(new AuthResponseDTO("Déconnexion réussie"));
    }
}
