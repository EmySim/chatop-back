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
    private final UserRepository userRepository;

    /**
     * Constructeur avec injection du repository.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("🔍 Recherche de l'utilisateur avec l'email : " + email);

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            logger.warning("Utilisateur non trouvé avec l'email : " + email);
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
        }

        User user = optionalUser.get();
        logger.info("✅ Utilisateur trouvé : " + user.getEmail());

        // La classe User doit implémenter UserDetails
        return user;
    }
}