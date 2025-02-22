package com.rental.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public DefaultSecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactiver CSRF (utile pour des APIs REST)
                .authorizeRequests(auth -> auth
                        .requestMatchers("/**") // Permet toutes les requêtes
                        .permitAll() // Autoriser tout le monde à accéder à toutes les routes
                );
        return http.build();
    }
}

