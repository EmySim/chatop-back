package com.rental.security;

import java.util.Collections;
import java.util.logging.Logger;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rental.entity.User;
import com.rental.repository.UserRepository;

/**
 * Service de chargement des utilisateurs depuis la base de données.
 */
@Service
public class UserDetailsLoader implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserDetailsLoader.class.getName());
    private final UserRepository userRepository;

    /**
     * Constructeur avec injection du repository utilisateur.
     *
     * @param userRepository Repository pour récupérer les utilisateurs.
     */
    public UserDetailsLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge les détails de l'utilisateur par email (utilisé pour Spring Security).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Démarrage du chargement de l'utilisateur avec l'email : " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warning("Utilisateur non trouvé avec l'email : " + email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
                });

                logger.info("Utilisateur trouvé avec l'email : " + email + ", ID : " + user.getId() + ", rôle : ROLE_" + user.getRole().name());
        
                return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    /**
     * Charge les détails de l'utilisateur par ID (utilisé pour Spring Security).
     */
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        logger.info("Démarrage du chargement de l'utilisateur avec l'ID : " + id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warning("Utilisateur non trouvé avec l'ID : " + id);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
                });

        logger.info("Utilisateur trouvé avec l'ID : " + user.getId() + ", email : " + user.getEmail() + ", rôle : ROLE_" + user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}