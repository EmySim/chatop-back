package com.rental.service;

import com.rental.dto.UserDTO;
import com.rental.entity.User;
import com.rental.entity.Role;
import com.rental.repository.UserRepository;
import com.rental.Mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur de UserService.
     * @param userRepository Le repository des utilisateurs.
     * @param passwordEncoder L'encodeur de mot de passe pour la sécurité.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inscription d'un utilisateur sans génération de JWT.
     * @param userDTO Données de l'utilisateur à inscrire.
     * @return UserDTO représentant l'utilisateur inscrit.
     */
    @Operation(summary = "Inscription d'un utilisateur", description = "Permet d'inscrire un utilisateur sans générer de JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur inscrit avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'inscription (email déjà utilisé ou mot de passe invalide)")
    })
    public UserDTO register(UserDTO userDTO) {
        logger.info("Tentative d'inscription pour l'utilisateur : " + userDTO.getEmail());

        // Crée l'utilisateur et le sauvegarde en base de données
        User user = createUser(userDTO.getEmail(), userDTO.getName(), userDTO.getPassword(), userDTO.getRole());

        logger.info("Utilisateur inscrit avec succès : " + userDTO.getEmail());

        // Retourne le DTO de l'utilisateur inscrit
        return UserMapper.toDTO(user);
    }

    /**
     * Recherche d'un utilisateur par son email.
     * @param email L'email de l'utilisateur à rechercher.
     * @return UserDTO représentant l'utilisateur trouvé.
     */
    @Operation(summary = "Recherche d'un utilisateur par email", description = "Permet de rechercher un utilisateur par son email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non autorisé")
    })
    public UserDTO findUserByEmail(String email) {
        logger.info("Recherche d'un utilisateur avec l'email : " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warning("Utilisateur non trouvé avec l'email : " + email);
                    return new IllegalStateException("Utilisateur non trouvé avec cet email : " + email);
                });

        return UserMapper.toDTO(user);
    }

    /**
     * Recherche d'un utilisateur par son ID.
     * @param id L'ID de l'utilisateur à rechercher.
     * @return UserDTO représentant l'utilisateur trouvé.
     */
    @Operation(summary = "Recherche d'un utilisateur par ID", description = "Permet de rechercher un utilisateur par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non autorisé")
    })
    public UserDTO findUserById(Long id) {
        logger.info("Recherche d'un utilisateur avec l'ID : " + id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warning("Utilisateur non trouvé avec l'ID : " + id);
                    return new IllegalStateException("Utilisateur non trouvé avec cet ID : " + id);
                });

        return UserMapper.toDTO(user);
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
