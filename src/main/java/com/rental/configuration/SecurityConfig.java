package com.rental.configuration;

import com.rental.service.JwtService;
import com.rental.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.logging.Logger;

/**
 * Configuration de la sécurité avec Spring Security.
 * Configure l'authentification via JWT et un UserDetailsService.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    /**
     * Constructeur avec injection des dépendances.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsServiceImpl userDetailsService,
                          JwtService jwtService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * Définit la chaîne de sécurité.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactivation CSRF (utile pour une API REST)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/public/**",
                                "/api/auth/register",
                                "/auth/login",
                                "/auth/register",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll() // Routes publiques
                        .anyRequest().authenticated() // Authentification requise pour les autres routes
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .addLogoutHandler((request, response, authentication) -> {
                            String authHeader = request.getHeader("Authorization");
                            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                String token = authHeader.substring(7);
                                jwtService.invalidateToken(token);
                                logger.info("Token invalidé lors de la déconnexion : " + token);
                            }
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Ajout du filtre JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Pas de session

        logger.info("Configuration de la sécurité chargée avec succès.");
        return http.build();
    }

    /**
     * Définit l'encodeur de mots de passe pour les utilisateurs.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Utilisation de BCrypt pour l'encodage sécurisé des mots de passe
    }

    /**
     * Définit le provider d'authentification utilisant le UserDetailsServiceImpl.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder()); // Utilisation de BCryptPasswordEncoder
        return authProvider;
    }

    /**
     * Gestionnaire d'authentification basé sur la configuration Spring Security.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
