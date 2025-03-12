package com.rental.service;

import java.util.Optional;

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
        // Récupérer l'objet Authentication du contexte de sécurité
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'utilisateur est authentifié
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié.");
        }

        // Récupérer le 'principal' de l'authentication
        Object principal = auth.getPrincipal();

        // Si le principal est une instance de l'objet User de Spring Security
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) principal;

            try {
                // Récupération de l'email ou principal, selon la configuration de Spring
                // Security
                String email = userDetails.getUsername(); // Utiliser getUsername() pour obtenir l'email

                // Recherche par email dans la base des utilisateurs
                User user = userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("Utilisateur non trouvé pour l'email : " + email));

                return user.getId();
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Erreur pendant l'authentification.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié.");
        }
    }

    /**
     * Crée un utilisateur avec un mot de passe crypté et le sauvegarde en base de
     * données.
     */
    public User createUser(String email, String name, String password, Role role) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un utilisateur avec cet email existe déjà.");
        }

        // Encoder le mot de passe
        String encryptedPassword = passwordEncoder.encode(password);

        // Créer un nouvel utilisateur
        User newUser = new User(email, name, encryptedPassword, role);

        // Sauvegarder dans la base de données
        return userRepository.save(newUser);
    }

    /**
     * Recherche un utilisateur par email et retourne l'entité `User`.
     */
    public User getEntityUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Utilisateur non trouvé avec cet email."));
    }

    /**
     * Recherche un utilisateur par email et retourne un DTO.
     */
    public UserDTO getUserByEmail(String email) {
        User user = getEntityUserByEmail(email);
        return convertToDTO(user);
    }

    /**
     * Récupère un utilisateur par ID et retourne un DTO.
     */
    public UserDTO getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'identifiant est invalide.");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé pour cet ID.");
        }

        User user = optionalUser.get();
        return convertToDTO(user);
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