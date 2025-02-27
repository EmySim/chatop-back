package com.rental.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
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

@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());
    private final Key secretKey;
    private final Set<String> invalidatedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration;

    public JwtService(@Value("${JWT_SECRET}") String secretKeyBase64) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        this.secretKey = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
        logger.info("JwtService initialisé avec succès : clé décodée." + secretKey);
    }

    public String generateToken(UserDetails userDetails) {
        // Générer un token sans tentative d'invalidation pendant le login
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        if (token == null || token.isEmpty()) {
            logger.warning("Le token JWT est vide ou nul !");
            return null;
        }
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (MalformedJwtException e) {
            logger.log(Level.SEVERE, "Erreur liée à un token JWT malformé : " + token, e);
            return null;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Erreur inattendue lors de l'extraction du username : " + token, ex);
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            if (username == null) {
                return false;
            }
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la validation du token : " + token, e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtException e) {
            logger.log(Level.WARNING, "Erreur lors de la vérification de l'expiration du token", e);
            return true; // Considérez un token comme expiré s'il ne peut pas être analysé correctement
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenWellFormed(String token) {
        // Vérifie si le token suit la syntaxe JWT à 3 parties séparées par "."
        return token != null && token.split("\\.").length == 3;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        if (!isTokenWellFormed(token)) {
            throw new MalformedJwtException("Le token JWT est mal formé.");
        }
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


    // Retirer l'appel d'invalidation du token lors du login
    // Cette méthode ne devrait être utilisée que pour invalider des tokens pendant la déconnexion ou un autre processus spécifique
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
        logger.info("Token invalidé : " + token);
    }

    // Vérification si le token a été invalidé
    public boolean isTokenInvalidated(String token) {
        boolean isInvalidated = invalidatedTokens.contains(token);
        logger.info("Vérification de l'invalidation du token : " + token + " - Résultat : " + isInvalidated);
        return isInvalidated;
    }

    public void logout(String token) {
        if (token != null && isTokenWellFormed(token)) {
            invalidateToken(token);
            logger.info("Utilisateur déconnecté et token invalidé : " + token);
        } else {
            logger.warning("Aucun token valide fourni pour la déconnexion.");
        }
    }

}
