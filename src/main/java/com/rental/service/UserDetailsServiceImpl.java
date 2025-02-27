package com.rental.service;

import com.rental.entity.User;
import com.rental.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Classe implémentant UserDetailsService pour charger un utilisateur par son email.
 */
@Service
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

        UserDTO userDTO = userService.findUserByEmail(email); // Utilisation du service au lieu du repo

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>() // Liste des rôles ou permissions
        );
    }
}