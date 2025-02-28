package com.rental.service;

import com.rental.entity.User;
import com.rental.dto.UserDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.logging.Logger;
import java.util.ArrayList;

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

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Authentification : Chargement de l'utilisateur via l'email : " + email);

        // Recherche de l'utilisateur par email
        UserDTO userDTO = userService.findUserDTOByEmail(email);

        if (userDTO == null) {
            logger.warning("Utilisateur non trouvé pour l'email : " + email);
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
        }

        // Conversion duUserDTO en User (pour récupérer le mot de passe de l'entité User)
        User user = userService.findUserByEmail(email);

        return new org.springframework.security.core.userdetails.User(
                userDTO.getEmail(),
                user.getPassword(), // Utilisation du mot de passe de l'entité User
                new ArrayList<>()
        );
    }
}
