package com.rental.controller;

import com.rental.service.JwtService;
import com.rental.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    // Constructeur avec injection des dépendances
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * Endpoint pour gérer l'authentification des utilisateurs et générer un token JWT.
     *
     * @param username Nom d'utilisateur envoyé dans le corps de la requête.
     * @param password Mot de passe envoyé dans le corps de la requête.
     * @return JSON contenant le token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        logger.info("Tentative d'authentification pour l'utilisateur : " + username);

        try {
            // Authentification de l'utilisateur via AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Si l'authentification réussit, récupérer les détails de l'utilisateur
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Générer un JWT valide avec JwtService
            String token = jwtService.generateToken(userDetails);

            // Structurer la réponse
            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            logger.info("JWT généré avec succès pour l'utilisateur : " + username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warning("Échec de l'authentification pour l'utilisateur : " + username);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }
}