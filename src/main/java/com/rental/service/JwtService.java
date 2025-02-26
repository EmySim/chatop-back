package com.rental.service;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service de gestion des tokens JWT.
 */
@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());

    private final Key secretKey;

    // Stockage des tokens invalidés pour éviter leur réutilisation après un logout
    private final Set<String> invalidatedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration;

    /**
     * Constructeur - Initialise la clé secrète à partir d'une base64 encodée.
     */
    public JwtService(@Value("${JWT_SECRET}") String secretKeyBase64) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        this.secretKey = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
        logger.info("JwtService initialisé avec succès : clé décodée.");
    }

    /**
     * Génère un token JWT pour un utilisateur donné.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait l'email (username) du token JWT.
     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (JwtException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'extraction du subject JWT", e);
            return null;
        }
    }

    /**
     * Vérifie si le token est valide (non expiré, non invalidé, et correspond à l'utilisateur).
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username != null
                && username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && !isTokenInvalidated(token);
    }

    /**
     * Vérifie si le token est expiré.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration du token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait une information spécifique des claims JWT.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims du token JWT.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'extraction des claims JWT", e);
            throw e;
        }
    }

    /**
     * Invalide un token en l'ajoutant à la liste des tokens invalidés.
     */
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
        logger.info("Token invalidé : " + token);
    }

    /**
     * Vérifie si un token a été invalidé.
     */
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }
}
