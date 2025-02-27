package com.rental.service;

import com.rental.entity.User;
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
@Primary // Ajout de l'annotation @Primary ici pour marquer cette classe comme le bean par défaut
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserDetailsServiceImpl.class.getName());
    private final UserService userService; // Utilise UserService au lieu de UserRepository.

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Authentification : Chargement de l'utilisateur via l'email : " + email);

        User user = userService.findUserByEmail(email); // Utilisation du service au lieu du repo

        if (user == null) {
            logger.warning("Utilisateur non trouvé pour l'email : " + email);
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>() // Liste des rôles ou permissions (si nécessaire, tu peux la remplir avec les rôles de l'utilisateur)
        );
    }
}
