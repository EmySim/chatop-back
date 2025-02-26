package com.rental.configuration;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
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
        logger.info("Début du processus de filtrage.");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Vérification de l'en-tête Authorization
        if (authHeader == null) {
            logger.warning("Authorization header est NULL !");
        } else {
            logger.info("Authorization Header : " + authHeader);
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warning("Authorization header manquant ou mal formaté.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token JWT
        jwt = authHeader.substring(7); // Supprime le préfixe "Bearer "
        try {
            userEmail = jwtService.extractUsername(jwt); // Extraire l'email depuis le token JWT
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'extraction de l'utilisateur du JWT", e);
            filterChain.doFilter(request, response);
            return;
        }

        // Check if the token is invalidated
        if (jwtService.isTokenInvalidated(jwt)) {
            logger.warning("Token JWT invalidé détecté : " + jwt);
            filterChain.doFilter(request, response);
            return;
        }

        // Valide le token et configure la sécurité
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.validateToken(jwt, userDetails)) {
                logger.info("✅ Token valide pour l'utilisateur : " + userEmail);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("JWT valide pour l'utilisateur : " + userEmail);
            } else {
                logger.warning("Token JWT invalide ou expiré pour l'utilisateur : " + userEmail);
            }
        }
        filterChain.doFilter(request, response); // Continue au filtre suivant
        logger.info("Fin du processus de filtrage.");
    }
}