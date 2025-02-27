package com.rental.service;

import com.rental.dto.AuthRegisterDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.entity.Role;
import com.rental.entity.User;
import com.rental.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Constructeur de AuthService.
     * @param userRepository Le repository des utilisateurs.
     * @param passwordEncoder L'encodeur de mot de passe pour la sécurité.
     * @param jwtService Le service pour la génération de tokens JWT.
     */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Inscription d'un nouvel utilisateur et génération d'un token JWT.
     * @param registerDTO Données de l'utilisateur à inscrire.
     * @return AuthResponseDTO contenant le token JWT.
     */
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
        User user = createUser(registerDTO.getEmail(), registerDTO.getName(), registerDTO.getPassword(), Role.USER);

        // Génère un token JWT pour l'utilisateur
        String jwtToken = jwtService.generateToken(user.getEmail());

        logger.info("Utilisateur inscrit avec succès : " + registerDTO.getEmail());

        // Retourne la réponse avec le JWT généré
        return new AuthResponseDTO(jwtToken);
    }

    /**
     * Méthode privée utilisée pour créer un utilisateur dans la base de données.
     * @param email L'email de l'utilisateur.
     * @param name Le nom de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @param role Le rôle de l'utilisateur.
     * @return L'utilisateur créé.
     */
    private User createUser(String email, String name, String password, Role role) {
        logger.info("Création de l'utilisateur : " + email);

        // Crée un utilisateur avec un mot de passe crypté
        User user = new User(email, name, passwordEncoder.encode(password), role);

        // Sauvegarde l'utilisateur en base de données
        return userRepository.save(user);
    }
}
