package com.rental.service;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.rental.dto.UserDTO;
import com.rental.entity.Role;
import com.rental.entity.User;
import com.rental.repository.UserRepository;

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
     * Récupère l'ID de l'utilisateur actuellement authentifié.
     *
     * @return ID de l'utilisateur authentifié si trouvé, lève une exception sinon.
     */
    public Long getAuthenticatedUserId() {
        logger.info("[DEBUG] Appel à getAuthenticatedUserId pour récupérer l'utilisateur connecté.");

        // Récupération de l'authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            logger.warning("[ERREUR] Aucun utilisateur authentifié trouvé dans le contexte.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié.");
        }

        try {
            // Récupération de l'email ou principal, selon la configuration de Spring Security
            String email = (String) auth.getPrincipal(); // Adapter selon la classe UserDetails/configuration
            logger.info("[DEBUG] Utilisateur authentifié avec l'email : " + email);

            // Recherche par email dans la base des utilisateurs
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé pour l'email : " + email));

            logger.info("[SUCCESS] Utilisateur authentifié trouvé avec l'ID : " + user.getId());
            return user.getId();
        } catch (Exception e) {
            logger.severe("[ERREUR] Erreur lors de la récupération de l'utilisateur authentifié : " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Erreur pendant l'authentification.");
        }
    }


    /**
     * Crée un utilisateur avec un mot de passe crypté et le sauvegarde en base de données.
     */
    public User createUser(String email, String name, String password, Role role) {
        logger.info("[DEBUG] Création d'un utilisateur avec email : " + email);

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(email)) {
            logger.warning("[ERREUR] L'utilisateur existe déjà avec cet email : " + email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un utilisateur avec cet email existe déjà.");
        }

        // Encoder le mot de passe
        String encryptedPassword = passwordEncoder.encode(password);

        // Créer un nouvel utilisateur
        User newUser = new User(email, name, encryptedPassword, role);

        // Sauvegarder dans la base de données
        User savedUser = userRepository.save(newUser);
        logger.info("[SUCCESS] Nouvel utilisateur créé : " + savedUser);

        return savedUser;
    }


    /**
     * Recherche un utilisateur par email et retourne l'entité `User`.
     */
    public User getEntityUserByEmail(String email) {
        logger.info("[DEBUG] Recherche d'utilisateur avec l'email : " + email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec cet email."));
    }

    /**
     * Recherche un utilisateur par email et retourne un DTO.
     */
    public UserDTO getUserByEmail(String email) {
        logger.info("[DEBUG] Recherche d'utilisateur (DTO) avec l'email : " + email);

        User user = getEntityUserByEmail(email);
        logger.info("[DEBUG] createdAt: " + user.getCreatedAt() + ", lastUpdated: " + user.getLastUpdated());
        UserDTO userDTO = convertToDTO(user);
        logger.info("[SUCCESS] Utilisateur trouvé et converti en DTO : " + userDTO);

        return userDTO;
    }

    /**
     * Récupère un utilisateur par ID et retourne un DTO.
     */
    public UserDTO getUserById(Long id) {
        logger.info("[DEBUG] Appel à getUserById avec l'ID : " + id);

        if (id == null || id <= 0) {
            logger.warning("[ERREUR] ID invalide détecté dans le service : " + id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant est invalide.");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.warning("[ERREUR] Aucun utilisateur trouvé pour l'ID : " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé pour cet ID.");
        }

        User user = optionalUser.get();
        logger.info("[SUCCESS] Utilisateur trouvé dans la base avec l'ID : " + user.getId());

        UserDTO userDTO = convertToDTO(user);
        logger.info("[SUCCESS] Conversion de l'utilisateur en DTO réussie : " + userDTO);

        return userDTO;
    }

    /**
     * Convertit une entité User en DTO.
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getLastUpdated(),
                user.getRole());
    }
}