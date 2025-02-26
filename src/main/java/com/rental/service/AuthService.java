package com.rental.service;

import java.util.logging.Logger;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rental.dto.AuthRegisterDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.entity.Role;
import com.rental.entity.User;
import com.rental.repository.UserRepository;

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
     * @param registerDTO Les informations d'inscription.
     * @return Un objet AuthResponseDTO contenant un message de succès.
     */
    public AuthResponseDTO register(AuthRegisterDTO registerDTO) {
        logger.info("Début de la méthode register pour : " + registerDTO.getEmail());

        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            logger.warning("Échec de l'inscription : l'email existe déjà.");
            throw new IllegalArgumentException("L'email est déjà utilisé.");
        }

        User user = new User(
                registerDTO.getEmail(),
                registerDTO.getName(),
                passwordEncoder.encode(registerDTO.getPassword()),
                Role.USER // Rôle par défaut
        );

        userRepository.save(user);
        logger.info("(ಥ﹏ಥ)Utilisateur enregistré avec succès : " + registerDTO.getEmail());

        return new AuthResponseDTO("(ÒДÓױ)Utilisateur enregistré avec succès.");
    }
}