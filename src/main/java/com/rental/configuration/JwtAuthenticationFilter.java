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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    private final JwtService jwtService;
    private final UserDetailsLoader userDetailsLoader;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsLoader userDetailsLoader) {
        this.jwtService = Objects.requireNonNull(jwtService, "JwtService ne peut pas être null");
        this.userDetailsLoader = Objects.requireNonNull(userDetailsLoader, "UserDetailsLoader ne peut pas être null");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        logger.info("🔍 Début du filtrage JWT.");

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warning("⚠️ Aucun token Bearer trouvé.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le JWT de l'en-tête
        final String jwt = authHeader.substring(7).trim();
        logger.info("🔑 Token JWT extrait.");

        // Vérification de l'encodage du JWT en Base64URL avant toute opération
        if (!isBase64UrlEncoded(jwt)) {
            logger.warning("❌ Token JWT invalide : encodage incorrect.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Encodage JWT invalide.");
            return;
        }

        final String userEmail;
        try {
            userEmail = jwtService.extractUsername(jwt);
            logger.info("👤 Utilisateur extrait du JWT : " + userEmail);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Erreur lors de l'extraction du username du JWT", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token JWT invalide.");
            return;
        }

        if (userEmail == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Vérification de la validité du JWT
        if (!jwtService.validateToken(jwt, userEmail)) {
            logger.warning("🚫 Tentative d'authentification échouée : JWT non valide.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Échec d'authentification.");
            return;
        }

        UserDetails userDetails = userDetailsLoader.loadUserByUsername(userEmail);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.info("✅ Authentification réussie.");

        filterChain.doFilter(request, response);
        logger.info("✅ Fin du filtrage JWT.");
    }

    /**
     * Vérifie si un JWT suit le format Base64URL (sans padding).
     * Un JWT doit contenir exactement trois parties séparées par des points.
     */
    private boolean isBase64UrlEncoded(String jwt) {
        return jwt.matches("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");
    }
}
