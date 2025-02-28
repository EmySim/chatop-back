package com.rental.service;

import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Service pour gérer les tokens JWT : génération, extraction et validation.
 */
@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());
    private final Key signingKey; // Clé secrète pour signer les tokens
    private final Set<String> invalidatedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>()); // Liste noire temporaire des tokens

    @Value("${JWT_EXPIRATION}")
    private long jwtExpirationTime; // Durée de validité en millisecondes

    /**
     * Constructeur de JwtService, initialise la clé de signature à partir de la clé secrète.
     * @param secretKeyBase64 Clé secrète encodée en base64 pour signer les tokens.
     */
    public JwtService(@Value("${JWT_SECRET}") String secretKeyBase64) {
        if (secretKeyBase64 == null || secretKeyBase64.isEmpty()) {
            logger.severe("❌ La clé secrète JWT est manquante !");
            throw new IllegalArgumentException("La clé secrète JWT doit être définie.");
        }
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyBase64));
        logger.info("✅ Clé secrète JWT configurée avec succès.");
    }

    /**
     * Génère un token JWT pour un utilisateur à partir de son username (email).
     *
     * @param subject Le sujet (e-mail ou identifiant utilisateur).
     * @param additionalClaims Données supplémentaires (facultatif).
     * @return Un token JWT signé.
     */
    public String generateToken(String subject, Map<String, Object> additionalClaims) {
        return Jwts.builder()
                .setClaims(additionalClaims)
                .setSubject(subject) // Défini le "subject" (e.g., email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256) // Signature avec la clé secrète
                .compact();
    }

    /**
     * Génère un token JWT uniquement avec le sujet.
     *
     * @param subject (e.g., email ou ID utilisateur).
     * @return Un token JWT signé.
     */
    public String generateToken(String subject) {
        return generateToken(subject, new HashMap<>()); // Pas de données supplémentaires
    }

    /**
     * Extrait une valeur spécifique ("claim") d'un token.
     *
     * @param token Le token JWT.
     * @param claimsResolver Une fonction pour extraire la donnée.
     * @param <T> Le type de la valeur extraite.
     * @return La donnée extraite des claims.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Récupère le sujet (email) contenu dans le token.
     *
     * @param token Token JWT.
     * @return Le sujet du token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Utilise getSubject directement
    }

    /**
     * Extrait toutes les données (claims) contenues dans un JWT.
     *
     * @param token Token JWT.
     * @return Les claims extraites.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si un token est valide.
     *
     * @param token Le token JWT.
     * @param userEmail email de l'utilisateur.
     * @return true si le token est valide, sinon false.
     */
    public boolean validateToken(String token, String userEmail) {
        String extractedUsername = extractUsername(token); // Récupère le subject
        return extractedUsername.equals(userEmail) && !isTokenExpired(token) && !isTokenInvalidated(token);
    }

    /**
     * Vérifie si un token a dépassé sa date d'expiration.
     *
     * @param token Token JWT.
     * @return true si expiré, sinon false.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration du token.
     *
     * @param token Token JWT.
     * @return La date d'expiration.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Vérifie si un token a été invalidé (liste noire).
     *
     * @param token Token JWT.
     * @return true si le token est révoqué.
     */
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }
}
