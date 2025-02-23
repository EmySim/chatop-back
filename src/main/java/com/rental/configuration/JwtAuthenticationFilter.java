package com.rental.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Filtre JWT appliqué pour valider les tokens sur chaque requête HTTP.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    private final com.rental.service.JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructeur avec injection des dépendances.
     */
    public JwtAuthenticationFilter(com.rental.service.JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        logger.info("Authorization Header: " + authHeader); // Vérification de l'en-tête


        // Vérifie si un header d'autorisation est présent
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("Authorization header manquant ou mal formaté.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token JWT
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt); // Extrait l'email depuis le token JWT

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Configure le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("JWT valide. L'utilisateur a été authentifié : " + userEmail);
            } else {
                logger.warning("Token JWT invalide ou expiré pour l'utilisateur : " + userEmail);
            }
        }else {
            logger.info("Aucun utilisateur trouvé ou utilisateur déjà authentifié.");
        }


        filterChain.doFilter(request, response);// Passe au filtre suivant
    }
}