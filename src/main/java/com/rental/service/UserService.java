package com.rental.service;

import com.rental.dto.UserDTO;
import com.rental.entity.User;
import com.rental.entity.Role;
import com.rental.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Service pour gérer les opérations liées aux utilisateurs.
 */
@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur pour injecter le UserRepository et le PasswordEncoder.
     * @param userRepository Le repository des utilisateurs.
     * @param passwordEncoder L'encodeur de mot de passe pour la sécurité.
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Méthode utilisée pour créer un utilisateur dans la base de données.
     * @param email L'email de l'utilisateur.
     * @param name Le nom de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @param role Le rôle de l'utilisateur.
     * @return L'utilisateur créé.
     */
    public User createUser(String email, String name, String password, Role role) {
        logger.info("Création de l'utilisateur : " + email);

        // Crée un utilisateur avec un mot de passe crypté
        User user = new User(email, name, passwordEncoder.encode(password), role);

        // Sauvegarde l'utilisateur en base de données
        return userRepository.save(user);
    }

    /**
     * Récupère un utilisateur complet (entité) via son email.
     * @param email L'email de l'utilisateur.
     * @return L'objet User (entité).
     */
    public User getEntityUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + email));
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
    public UserDTO getUserByEmail(String email) {
        logger.info("Recherche d'un utilisateur avec l'email : " + email);

        // Récupération de l'utilisateur depuis le repository.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warning("Aucun utilisateur trouvé avec l'email : " + email);
                    return new IllegalArgumentException("Utilisateur non trouvé pour l'email : " + email);
                });

        logger.info("Utilisateur trouvé : " + user.getEmail());

        return convertToDTO(user);

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

        // Transformation de l'entité User en DTO
        return convertToDTO(user);
    }

    /**
     * Convertit un utilisateur en DTO.
     * @param user L'entité utilisateur.
     * @return UserDTO représentant l'utilisateur.
     */
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setLastUpdated(user.getLastUpdated());

        return userDTO;
    }
}
