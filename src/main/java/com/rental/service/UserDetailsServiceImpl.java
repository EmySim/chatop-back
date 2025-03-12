package com.rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rental.entity.User;

/**
 * Classe implémentant UserDetailsService pour charger un utilisateur par son email.
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    private static final String USER_NOT_FOUND_MESSAGE = "Utilisateur non trouvé avec l'email : ";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Appel du service pour récupérer l'utilisateur (par entité)
        User user = userService.getEntityUserByEmail(email);
        if (user == null) {
            handleUserNotFound(email);
        }

        // Transforme l'utilisateur en UserDetails pour Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // Mot de passe encodé depuis l'entité
                .roles(user.getRole().toString()) // Assumez que Role est une énumération ou une chaîne
                .build();
    }

    /**
     * Gère le cas où un utilisateur n'est pas trouvé.
     * @param email L'email de l'utilisateur.
     * @throws UsernameNotFoundException Exception avec message prédéfini.
     */
    private void handleUserNotFound(String email) throws UsernameNotFoundException {
        throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE + email);
    }
}