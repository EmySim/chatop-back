package com.rental.configuration;

import java.util.logging.Logger;

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

import com.rental.security.UserDetailsLoader;
import com.rental.service.JwtService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsLoader userDetailsLoader;
    private final JwtService jwtService;

    /**
     * Constructeur de configuration de sécurité avec injection des dépendances.
     *
     * @param jwtAuthenticationFilter Filtre pour gérer l'authentification JWT
     * @param userDetailsLoader Service de chargement des utilisateurs
     * @param jwtService Service pour la gestion des tokens JWT
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsLoader userDetailsLoader,
                          JwtService jwtService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsLoader = userDetailsLoader;
        this.jwtService = jwtService;
    }

    /**
     * Définit la configuration de sécurité de l'application.
     *
     * - Désactive CSRF car on utilise JWT.
     * - Définit les autorisations des endpoints.
     * - Ajoute un filtre d'authentification JWT.
     * - Configure une politique de session stateless.
     * - Gère la déconnexion avec suppression du token.
     *
     * @param http Configuration HTTP de Spring Security.
     * @return Le filtre de sécurité configuré.
     * @throws Exception En cas d'erreur de configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Désactivation de la protection CSRF (inutilisée avec JWT)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout") // Endpoint de déconnexion
                        .invalidateHttpSession(true) // Invalidation de la session
                        .deleteCookies("JSESSIONID") // Suppression du cookie de session
                        .addLogoutHandler((request, response, authentication) -> {
                            // Extraction du token depuis le header Authorization
                            String authHeader = request.getHeader("Authorization");
                            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                String token = authHeader.substring(7);
                                jwtService.invalidateToken(token); // Invalidation du token
                                logger.info("Token JWT invalidé lors de la déconnexion : " + token);
                            }
                        })
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT => pas de session côté serveur
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Ajout du filtre d'authentification JWT

        logger.info("Configuration de la sécurité chargée avec succès.");
        return http.build();
    }

    /**
     * Définit l'encodeur de mots de passe (BCrypt).
     *
     * @return Un encodeur BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Définit le fournisseur d'authentification basé sur UserDetailsLoader.
     *
     * @return Le provider d'authentification DAO.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsLoader);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Définit l'AuthenticationManager pour Spring Security.
     *
     * @param authenticationConfiguration Configuration d'authentification.
     * @return L'AuthenticationManager configuré.
     * @throws Exception En cas d'erreur de configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
