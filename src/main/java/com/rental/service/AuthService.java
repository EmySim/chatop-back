package com.rental.service;

import java.util.logging.Logger;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rental.dto.AuthRegisterDTO;
import com.rental.dto.AuthResponseDTO;
import com.rental.entity.Role;
import com.rental.entity.User;
import com.rental.repository.UserRepository;

@Service
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // Ajout de la déclaration du champ jwtService

    // Injection de JwtService dans le constructeur
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService; // Initialisation du champ jwtService
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

        // Crée un nouvel utilisateur avec le rôle par défaut
        User user = new User(
                registerDTO.getEmail(),
                registerDTO.getName(),
                passwordEncoder.encode(registerDTO.getPassword()),
                Role.USER
        );

        // Sauvegarde l'utilisateur en base
        userRepository.save(user);
        logger.info("Utilisateur mis en base avec succès : " + registerDTO.getEmail());

        // Génère un token JWT à partir du service JwtService
        String jwtToken = jwtService.generateToken(user); // Appel de la méthode generateToken du JwtService

        // Renvoie un DTO avec le token JWT valide
        return new AuthResponseDTO(jwtToken);
    }
}
