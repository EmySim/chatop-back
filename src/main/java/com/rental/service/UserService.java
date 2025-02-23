package com.rental.service;

import com.rental.dto.UserDTO;
import com.rental.entity.User;
import com.rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

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
     * Méthode pour enregistrer un nouvel utilisateur.
     * Cette méthode vérifie si l'email est déjà utilisé, puis crée un nouvel utilisateur.
     *
     * @param userDTO Contient les données nécessaires pour créer un utilisateur.
     */
    public void register(UserDTO userDTO) {
        logger.info("Début de l'enregistrement de l'utilisateur : " + userDTO.getEmail());

        // Vérifie si un utilisateur existe déjà avec cet email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            logger.warning("Échec de l'enregistrement : email déjà utilisé - " + userDTO.getEmail());
            throw new IllegalStateException("Cet email est déjà utilisé !");
        }

        // Création de l'utilisateur avec les données du DTO
        User user = new User(userDTO.getEmail(), userDTO.getName(), passwordEncoder.encode(userDTO.getPassword()));

        // Enregistrement de l'utilisateur dans la base de données
        userRepository.save(user);

        logger.info("Utilisateur enregistré avec succès : " + userDTO.getEmail());
    }

    /**
     * Recherche un utilisateur par email et convertit en UserDTO.
     * Cette méthode permet de récupérer les informations d'un utilisateur sans exposer le mot de passe.
     *
     * @param email L'adresse email de l'utilisateur à rechercher.
     * @return DTO contenant les informations de l'utilisateur (email et nom).
     * @throws IllegalStateException Si l'utilisateur n'est pas trouvé avec cet email.
     */
    public UserDTO findUserDTOByEmail(String email) {
        logger.info("Recherche d'un utilisateur avec l'email : " + email);

        // Recherche de l'utilisateur dans la base de données
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    // Si l'utilisateur n'est pas trouvé, on génère une exception
                    logger.warning("Utilisateur non trouvé avec l'email : " + email);
                    return new IllegalStateException("Utilisateur non trouvé avec l'email : " + email);
                });

        // Retourne les informations de l'utilisateur sous forme de DTO (mot de passe masqué)
        logger.info("Utilisateur trouvé : " + email);
        return new UserDTO(user.getEmail(), user.getName(), null); // Le mot de passe est masqué (null)
    }
}
