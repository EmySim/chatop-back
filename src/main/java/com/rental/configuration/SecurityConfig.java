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

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsLoader userDetailsLoader,
                          JwtService jwtService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsLoader = userDetailsLoader;
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("🔒 Initialisation de la configuration de sécurité...");

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Liste des routes accessibles sans authentification
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/public/**", // Tous les endpoints commençant par /public/
                                "/swagger-ui/**", // Autoriser Swagger UI
                                "/v3/api-docs/**" // Autoriser la documentation OpenAPI
                        ).permitAll()
                        // Tout le reste doit être authentifié
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        logger.info("Configuration de la sécurité chargée avec succès.");
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("🔑 Utilisation de BCryptPasswordEncoder.");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsLoader);
        authProvider.setPasswordEncoder(passwordEncoder());
        logger.info("✅ AuthenticationProvider configuré.");
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        logger.info("🔑 Initialisation de l'AuthenticationManager.");
        return authenticationConfiguration.getAuthenticationManager();
    }
}
