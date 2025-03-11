package com.rental.service;

import com.rental.dto.UserDTO;
import com.rental.entity.User;
import com.rental.entity.Role;
import com.rental.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.logging.Logger;
import java.util.Optional;


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
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crée un utilisateur avec un mot de passe crypté et le sauvegarde en base de données.
     */
    public User createUser(String email, String name, String password, Role role) {
        logger.info("Création de l'utilisateur avec l'email : " + email);
        User user = new User(email, name, encodePassword(password), role);
        return userRepository.save(user);
    }

    /**
     * Récupère un utilisateur (entité) via son email.
     */
    public User getEntityUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));
    }

    /**
     * Recherche un utilisateur par email et retourne un DTO.
     */
    @Operation(summary = "Recherche d'un utilisateur par email", description = "Permet de rechercher un utilisateur par son email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non autorisé")
    })
    public UserDTO getUserByEmail(String email) {
        logger.info("Recherche d'un utilisateur avec l'email : " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warning("Aucun utilisateur trouvé avec l'email : " + email);
                    return new UsernameNotFoundException("Utilisateur non trouvé pour l'email : " + email);
                });
        return convertToDTO(user);
    }

    /**
     * Recherche un utilisateur par son ID et retourne un DTO.
     *  @param id L'identifiant de la location.
     *      * @return DTO représentant la location si trouvée.
     */
    @Operation(summary = "Recherche d'un utilisateur par ID", description = "Permet de rechercher un utilisateur par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non autorisé")
    })
    public UserDTO getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'id : " + id);
        }
        // Convertir l'entité `User` en DTO et la retourner
        return new UserDTO(user.get());
    }


    /**
     * Récupère l'utilisateur actuellement authentifié et retourne un DTO.
     */
    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Aucun utilisateur authentifié trouvé.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        return user.getId();
    }

    /**
     * Convertit une entité User en DTO.
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt(), user.getLastUpdated(), user.getRole());
    }

    /**
     * Encode le mot de passe de manière sécurisée.
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
