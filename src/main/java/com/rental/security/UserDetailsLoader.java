package com.rental.security;

import java.util.Collections;

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
     *
     * @param email Email de l'utilisateur à charger.
     * @return UserDetails de l'utilisateur.
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    /**
     * Charge les détails de l'utilisateur par ID (utilisé pour Spring Security).
     *
     * @param id ID de l'utilisateur à charger.
     * @return UserDetails de l'utilisateur.
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé.
     */
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'ID : " + id));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}