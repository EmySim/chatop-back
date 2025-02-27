package com.rental.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rental.dto.UserDTO;
import com.rental.entity.User;
import com.rental.repository.UserRepository;

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
     * Enregistre un nouvel utilisateur.
     *
     * @param userDTO Les données de l'utilisateur.
     * @return L'utilisateur enregistré sous forme de DTO.
     */
    public UserDTO register(UserDTO userDTO) {
        logger.info("Début de l'enregistrement de l'utilisateur : " + userDTO.getEmail());

        // Vérifie l'unicité de l'email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            logger.warning("Échec de l'enregistrement : email déjà utilisé - " + userDTO.getEmail());
            throw new IllegalStateException("Cet email est déjà utilisé !");
        }

        // Création de l'entité utilisateur
        User user = new User(
                userDTO.getEmail(),
                userDTO.getName(),
                passwordEncoder.encode("defaultPassword"), // Remplacez par un mot de passe configurable ou généré
                userDTO.getRole()
        );

        // Sauvegarde dans la base de données
        user = userRepository.save(user);

        logger.info("Utilisateur enregistré avec succès : " + userDTO.getEmail());
        return toUserDTO(user); // Conversion en DTO
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

        return toUserDTO(user); // Conversion en DTO
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

        return toUserDTO(user); // Conversion en DTO
    }
    /**
     * Conversion d'une entité User en DTO.
     */
    private UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }


}
