package com.rental.service;

import com.rental.dto.UserDTO;
import com.rental.entity.User;
import com.rental.repository.UserRepository;
import com.rental.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * Service pour gérer les utilisateurs, y compris l'enregistrement et la récupération.
 */
@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inscrit un nouvel utilisateur en base.
     *
     * @param userDTO Données du nouvel utilisateur.
     * @return DTO de l'utilisateur inscrit.
     */
    public UserDTO register(UserDTO userDTO) {
        logger.info("Tentative d'enregistrement pour : " + userDTO.getEmail());

        // Vérifie la disponibilité de l'email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            logger.warning("Échec : cet email est déjà enregistré !");
            throw new IllegalStateException("L'email est déjà utilisé.");
        }

        // Vérification du mot de passe
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide !");
        }

        // Création de l'entité utilisateur
        User user = new User(
                userDTO.getEmail(),
                userDTO.getName(),
                passwordEncoder.encode("defaultPassword"), // Remplacez par un mot de passe configurable ou généré
                userDTO.getRole()
        );

        // Sauvegarde de l'utilisateur en base
        user = userRepository.save(user);
        logger.info("Utilisateur enregistré avec succès : " + userDTO.getEmail());

        return UserMapper.toDTO(user); // Convertit en DTO

    }


    /**
     * Recherche un utilisateur par email.
     *
     * @param email Adresse email.
     * @return Les informations de l'utilisateur sous forme de DTO.
     */
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
     * Recherche un utilisateur par ID.
     *
     * @param id Identifiant utilisateur.
     * @return Les informations de l'utilisateur sous forme de DTO.
     */
    public UserDTO findUserById(Long id) {
        logger.info("Recherche d'un utilisateur avec l'ID : " + id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warning("Utilisateur non trouvé avec l'ID : " + id);
                    return new IllegalStateException("Utilisateur non trouvé avec cet ID : " + id);
                });

        return UserMapper.toDTO(user);
    }
}
