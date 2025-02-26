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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    private final com.rental.service.JwtService jwtService;
    private final UserDetailsService userDetailsService;

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
        logger.info("Début du filtrage JWT.");

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("Aucun token Bearer trouvé dans l'en-tête Authorization.");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Récupérer ce qui suit "Bearer ".

        // Vérifiez si le token contient exactement deux points (.)
        if (jwt.chars().filter(ch -> ch == '.').count() != 2) {
            logger.warning("Le token JWT est mal formé : "+jwt);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid JWT token format.");
            return;
        }

        // Vérifiez que le JWT semble être en Base64URL.
        if (!isBase64UrlEncoded(jwt)) {
            logger.warning("Le token ne semble pas être encodé en Base64URL : " + jwt);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid JWT encoding.");
            return;
        }


        logger.info("Token JWT trouvé, extraction du nom d'utilisateur.");


        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'extraction de l'utilisateur du JWT", e);
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtService.isTokenInvalidated(jwt)) {
            logger.warning("Token invalidé détecté pour l'utilisateur : " + userEmail);
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Authentification non présente dans le contexte pour l'utilisateur : " + userEmail);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.validateToken(jwt, userDetails)) {
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

    /**
     * Vérifie si une chaîne est encodée en Base64URL et ne contient que des caractères valides.
     */
    private boolean isBase64UrlEncoded(String jwt) {
        return jwt.matches("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");
    }
}
