package com.rental.configuration;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rental.security.UserDetailsLoader;
import com.rental.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    private final JwtService jwtService;
    private final UserDetailsLoader userDetailsLoader;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param jwtService Service de gestion des tokens JWT.
     * @param userDetailsLoader Service de récupération des détails utilisateurs.
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsLoader userDetailsLoader) {
        this.jwtService = jwtService;
        this.userDetailsLoader = userDetailsLoader;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        logger.info("Début du filtrage JWT.");

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            logger.warning("L'en-tête Authorization est manquant.");
        } else {
            logger.info("En-tête Authorization trouvé : " + authHeader);
        }

        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("Aucun token Bearer trouvé dans l'en-tête Authorization.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le JWT de l'en-tête
        jwt = authHeader.substring(7); // Récupérer ce qui suit "Bearer ".
        logger.info("Token JWT extrait : " + jwt);


        // Étape 2 : Extraire les informations après validation
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'extraction de l'utilisateur du JWT", e);
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Authentification non présente dans le contexte pour l'utilisateur : " + userEmail);
            UserDetails userDetails = userDetailsLoader.loadUserByUsername(userEmail);

            if (jwtService.validateToken(jwt, userEmail)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Token validé pour l'utilisateur : " + userEmail);
            } else {
                logger.warning("Token invalide pour l'utilisateur : " + userEmail);
            }
        }
        filterChain.doFilter(request, response);
        logger.info("Fin du filtrage JWT.");
    }
}
