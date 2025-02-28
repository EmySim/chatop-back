package com.rental.security;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rental.entity.User;
import com.rental.repository.UserRepository;

/**
 * Service de chargement des utilisateurs depuis la base de données.
 */
@Service // 🔥 Ajout de @Service pour que Spring puisse l'injecter
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warning("Utilisateur non trouvé : " + email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
                });
    }
}
