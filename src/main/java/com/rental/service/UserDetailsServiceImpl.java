package com.rental.service;

import com.rental.dto.UserDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;

/**
 * Classe implémentant UserDetailsService pour charger un utilisateur par son email.
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserDetailsServiceImpl.class.getName());
    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    private static final String USER_NOT_FOUND_MESSAGE = "Utilisateur non trouvé avec l'email : ";
    private static final String USER_LOADING_MESSAGE = "Authentification : Chargement de l'utilisateur via l'email : ";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            logger.info("Chargement de l'utilisateur pour l'email : " + email);

            // Appel du service pour récupérer l'utilisateur sous forme de DTO
            UserDTO userDTO = userService.getUserByEmail(email);

            logger.info("Utilisateur trouvé : " + userDTO.getEmail());

            // Transforme le UserDTO en UserDetails pour Spring Security
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userDTO.getEmail())
                    .password(userDTO.getPassword()) // Assurez-vous que UserDTO contient le mot de passe.
                    .roles(userDTO.getRole())       // Assurez-vous que UserDTO contient le rôle sous forme de chaîne.
                    .build();
        } catch (IllegalArgumentException ex) {
            logger.warning("Utilisateur introuvable pour l'email : " + email);
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + email);
        }
    }


/**
     * Gère le cas où un utilisateur n'est pas trouvé.
     * @param email L'email de l'utilisateur.
     * @throws UsernameNotFoundException Exception avec message prédéfini.
     */
    private void handleUserNotFound(String email) throws UsernameNotFoundException {
        logger.warning(USER_NOT_FOUND_MESSAGE + email);
        throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE + email);
    }


}
