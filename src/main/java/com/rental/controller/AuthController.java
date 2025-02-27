package com.rental.controller;

import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Contrôleur pour la gestion de l'authentification et des utilisateurs.
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
     */
    @Operation(summary = "Enregistrer un nouvel utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur enrollé avec succès"),
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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtService.generateToken(((UserDetails) authentication.getPrincipal()).getUsername());


            logger.info("Connexion réussie pour : " + loginDTO.getEmail());
            return ResponseEntity.ok(new AuthResponseDTO(jwtToken));

        } catch (Exception e) {
            logger.warning("Échec de l'authentification pour : " + loginDTO.getEmail() + " - Erreur : " + e.getMessage());
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
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        logger.info("Récupération de l'utilisateur authentifié...");

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warning("Aucun utilisateur authentifié trouvé.");
            return ResponseEntity.status(401).build(); // Renvoie 401 si non authentifié
        }

        String authenticatedEmail = authentication.getName();
        UserDTO userDTO = new UserDTO(userService.findUserByEmail(authenticatedEmail));

        logger.info("Utilisateur connecté récupéré : ID = " + userDTO.getId() + ", Email = " + userDTO.getEmail());
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Déconnexion de l'utilisateur.
     */
    @Operation(summary = "Déconnexion de l'utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
            @ApiResponse(responseCode = "401", description = "Non autorisé")
    })
    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDTO> logout(HttpServletRequest request, Authentication authentication) {
        logger.info("Requête de déconnexion reçue.");

        // Vérifie si l'utilisateur est authentifié
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warning("Aucun utilisateur authentifié trouvé.");
            return ResponseEntity.status(401).body(new AuthResponseDTO("Non autorisé"));
        }

        // Récupère le token JWT du header "Authorization"
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warning("Authorization header manquant ou mal formaté.");
            return ResponseEntity.status(401).body(new AuthResponseDTO("Non autorisé"));
        }

        // Extrait le token en supprimant le préfixe "Bearer "
        String token = authHeader.substring(7);
        try {
            // Invalidation du token (método d'ajout à une liste noire, par exemple via Redis)
            jwtService.invalidateToken(token);
            logger.info("Token invalidé : " + token);

            // Efface le contexte de sécurité
            SecurityContextHolder.clearContext();
            logger.info("Déconnexion réussie pour le token : " + token);

            // Réponse de succès
            return ResponseEntity.ok(new AuthResponseDTO("Déconnexion réussie"));
        } catch (Exception e) {
            logger.severe("Erreur lors de l'invalidation du token: " + e.getMessage());
            return ResponseEntity.status(500).body(new AuthResponseDTO("Erreur serveur"));
        }
    }
}