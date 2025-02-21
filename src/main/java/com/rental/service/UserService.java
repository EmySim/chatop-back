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
     *
     * @param userDTO Contient les données nécessaires pour créer un utilisateur.
     */
    public void register(UserDTO userDTO) {
        logger.info("Début de l'enregistrement de l'utilisateur : " + userDTO.getEmail());

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            logger.warning("Échec : email déjà utilisé - " + userDTO.getEmail());
            throw new IllegalStateException("Cet email est déjà utilisé !");
        }

        User user = new User(userDTO.getEmail(), userDTO.getName(), passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);

        logger.info("Utilisateur enregistré avec succès : " + userDTO.getEmail());
    }

    /**
     * Recherche un utilisateur par email et convertit en UserDTO.
     *
     * @param email L'adresse email de l'utilisateur.
     * @return DTO contenant les informations de l'utilisateur.
     */
    public UserDTO findUserDTOByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé avec l'email : " + email));

        return new UserDTO(user.getEmail(), user.getName(), null); // Mot de passe masqué
    }
}