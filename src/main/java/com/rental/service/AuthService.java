package com.rental.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

@Service
public class AuthService {

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
        // Vérifie si l'email existe déjà dans la base de données
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new IllegalArgumentException("L'email est déjà utilisé.");
        }

        // Crée un utilisateur avec le rôle par défaut
        User user = userService.createUser(registerDTO.getEmail(), registerDTO.getName(), registerDTO.getPassword(), Role.USER);

        // Génère un token JWT pour l'utilisateur
        String jwtToken = jwtService.generateToken(user.getEmail());

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
        // Récupère l'utilisateur par email
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'email : " + loginDTO.getEmail()));

        // Vérifie le mot de passe
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe incorrect.");
        }

        // Génère un token JWT pour l'utilisateur
        String jwtToken = jwtService.generateToken(user.getEmail());

        // Retourne la réponse avec le JWT généré
        return new AuthResponseDTO(jwtToken);
    }

    // Méthode pour récupérer les informations de l'utilisateur actuellement authentifié
    public UserDTO getCurrentUser(String email) {
        return userService.getUserByEmail(email);
    }

    /**
     * Récupère l'ID de l'utilisateur actuellement connecté.
     *
     * @return ID de l'utilisateur authentifié.
     * @throws IllegalStateException si l'utilisateur authentifié est introuvable dans la base de données.
     */
    public Long getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return userRepository.findByEmail(((UserDetails) principal).getUsername())
                    .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé")).getId();
        } else {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
    }

    /**
     * Récupère l'utilisateur actuellement connecté.
     *
     * @return L'utilisateur authentifié.
     * @throws IllegalStateException si l'utilisateur authentifié est introuvable dans la base de données.
     */
    public User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return userRepository.findByEmail(((UserDetails) principal).getUsername())
                    .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé"));
        } else {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
    }

    /**
     * Vérifie si un utilisateur est autorisé à accéder à une ressource.
     *
     * @param ownerId L'ID du propriétaire de la ressource.
     * @param resourceId L'ID de la ressource.
     * @return true si l'utilisateur est autorisé, false sinon.
     */
    public boolean isAuthorized(Long ownerId, Long resourceId) {
        // Logique de vérification de l'autorisation
        // Par exemple, vérifier si l'utilisateur est le propriétaire de la ressource
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + ownerId));
        return user.getId().equals(ownerId);
    }
}