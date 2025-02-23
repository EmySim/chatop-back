package com.rental.controller;

import com.rental.dto.AuthRequestDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.dto.UserDTO;
import com.rental.service.JwtService;
import com.rental.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * Controller for handling authentication-related requests.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final AuthenticationManager authenticationManager; // Permet de gérer l'authentification des utilisateurs.
    private final JwtService jwtService;                      // Service pour gérer les tokens JWT.
    private final UserService userService;                    // Service pour la gestion des utilisateurs.

    // Injection des Beans nécessaires via le constructeur
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * Endpoint pour enregistrer un nouvel utilisateur.
     *
     * @param userDTO Les données du nouvel utilisateur à enregistrer.
     * @return Un statut HTTP approprié.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        logger.info("Tentative d'enregistrement d'un nouvel utilisateur : " + userDTO.getEmail());
        // Appelle un service pour enregistrer l'utilisateur (à implémenter dans UserService)
        userService.register(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur enregistré avec succès.");
    }

    /**
     * Endpoint pour connecter un utilisateur.
     *
     * @param request Les informations d'identification (email, mot de passe).
     * @return Un token JWT si l'authentification réussit.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        logger.info("Tentative de connexion pour : " + request.getEmail());

        // Authentifie l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Récupère les détails de l'utilisateur après authentification
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Génère un token JWT à partir des UserDetails
        String token = jwtService.generateToken(userDetails);


        logger.info("Utilisateur authentifié avec succès. Token généré.");
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    /**
     * Endpoint pour obtenir les informations de l'utilisateur actuellement authentifié.
     *
     * @param authentication Les informations d'authentification actuelles.
     * @return Les détails de l'utilisateur connecté.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        logger.info("Récupération des informations de l'utilisateur connecté.");
        String email = authentication.getName();
        UserDTO userDTO = userService.findUserDTOByEmail(email); // Utilise une méthode de conversion User -> UserDTO
        return ResponseEntity.ok(userDTO);

    }
}