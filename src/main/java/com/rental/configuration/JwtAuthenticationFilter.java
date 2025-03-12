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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Exclure toutes les routes nécessaires pour Swagger
        if (isSwaggerEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // Vérifier la présence de l'en-tête Authorization et son format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le JWT de l'en-tête
        final String jwt = authHeader.substring(7).trim();

        // Vérification de l'encodage du JWT en Base64URL avant toute opération
        if (!isBase64UrlEncoded(jwt)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Encodage JWT invalide.");
            return;
        }

        final String userEmail;
        try {
            // Extraire le nom d'utilisateur du JWT
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token JWT invalide.");
            return;
        }

        // Vérifier si l'utilisateur est déjà authentifié
        if (userEmail == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Vérification de la validité du JWT
        if (!jwtService.validateToken(jwt, userEmail)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Échec d'authentification.");
            return;
        }

        // Charger les détails de l'utilisateur et définir l'authentification dans le contexte de sécurité
        UserDetails userDetails = userDetailsLoader.loadUserByUsername(userEmail);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    /**
     * Vérifie si l'URI correspond à un endpoint Swagger à exclure.
     */
    private boolean isSwaggerEndpoint(String requestURI) {
        return requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars/") ||
                requestURI.startsWith("/configuration/ui") ||
                requestURI.startsWith("/configuration/security");
    }

    /**
     * Vérifie si un JWT suit le format Base64URL (sans padding).
     * Un JWT doit contenir exactement trois parties séparées par des points.
     */
    private boolean isBase64UrlEncoded(String jwt) {
        return jwt.matches("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");
    }
}