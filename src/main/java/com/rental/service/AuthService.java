package com.rental.service;

import java.util.logging.Logger;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rental.dto.AuthRegisterDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.dto.UserDTO;
import com.rental.entity.Role;
import com.rental.entity.User;
import com.rental.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.rental.dto.AuthLoginDTO;
import com.rental.service.JwtService;
import com.rental.service.UserService;

@Service
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;

    // Constructeur avec toutes les dépendances nécessaires
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    // Méthode d'inscription d'un utilisateur
    @Operation(summary = "Inscription d'un utilisateur", description = "Permet d'inscrire un nouvel utilisateur et génère un JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur inscrit avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'inscription (email déjà utilisé ou mot de passe invalide)")
    })
    public AuthResponseDTO register(AuthRegisterDTO registerDTO) {
        logger.info("Tentative d'inscription pour l'utilisateur : " + registerDTO.getEmail());

        // Vérifie si l'email existe déjà dans la base de données
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            logger.warning("L'email " + registerDTO.getEmail() + " est déjà utilisé.");
            throw new IllegalArgumentException("L'email est déjà utilisé.");
        }

        // Crée un utilisateur avec le rôle par défaut
        User user = userService.createUser(registerDTO.getEmail(), registerDTO.getName(), registerDTO.getPassword(), Role.USER);

        // Génère un token JWT pour l'utilisateur
        String jwtToken = jwtService.generateToken(user.getEmail());

        logger.info("Utilisateur inscrit avec succès : " + registerDTO.getEmail());

        // Retourne la réponse avec le JWT généré
        return new AuthResponseDTO(jwtToken);
    }

    // Méthode de connexion d'un utilisateur
    @Operation(summary = "Connexion d'un utilisateur", description = "Permet de connecter un utilisateur et génère un JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Échec de l'authentification")
    })
    public AuthResponseDTO login(AuthLoginDTO loginDTO) {
        logger.info("Tentative de connexion pour l'utilisateur : " + loginDTO.getEmail());

        // Récupère l'utilisateur par email
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'email : " + loginDTO.getEmail()));

        // Vérifie le mot de passe
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            logger.warning("Échec de l'authentification pour l'utilisateur : " + loginDTO.getEmail());
            throw new IllegalArgumentException("Mot de passe incorrect.");
        }

        // Génère un token JWT pour l'utilisateur
        String jwtToken = jwtService.generateToken(user.getEmail());

        logger.info("Connexion réussie pour l'utilisateur : " + loginDTO.getEmail());

        // Retourne la réponse avec le JWT généré
        return new AuthResponseDTO(jwtToken);
    }

    // Méthode pour récupérer les informations de l'utilisateur actuellement authentifié
    public UserDTO getCurrentUser(String email) {
        logger.info("Récupération de l'utilisateur connecté : " + email);
        return userService.getUserByEmail(email);
    }
}
