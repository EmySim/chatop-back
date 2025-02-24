package com.rental.service;

import com.rental.dto.AuthRequestDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.entity.User;
import com.rental.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Service pour gérer les opérations d'authentification et d'inscription.
 */
@Service
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inscrit un nouvel utilisateur.
     *
     * @param authRequestDTO Les informations d'inscription.
     * @return Un objet AuthResponseDTO contenant un message de succès.
     */
    public AuthResponseDTO register(AuthRequestDTO authRequestDTO) {
        logger.info("Début de la méthode register pour : " + authRequestDTO.getEmail());

        // Vérifiez si l'email existe déjà
        if (userRepository.existsByEmail(authRequestDTO.getEmail())) {
            logger.warning("Échec de l'inscription : l'email existe déjà.");
            throw new IllegalArgumentException("L'email est déjà utilisé.");
        }

        // Création de l'utilisateur
        User user = new User(
                authRequestDTO.getEmail(),
                authRequestDTO.getName(),
                passwordEncoder.encode(authRequestDTO.getPassword())
        );

        // Enregistrement dans la base de données
        userRepository.save(user);
        logger.info("Utilisateur enregistré avec succès : " + authRequestDTO.getEmail());

        return new AuthResponseDTO("Utilisateur enregistré avec succès.");
    }
}