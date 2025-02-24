package com.rental.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());

    private final Key secretKey;

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration;

    public JwtService(@Value("${JWT_SECRET}") String secretKeyBase64) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        this.secretKey = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
        logger.info("JwtService initialisé avec succès avec une clé décodée.");
    }

    public String generateToken(UserDetails userDetails) {
        logger.info("Génération du token avec la clé : " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));
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

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration == null || expiration.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            logger.log(Level.SEVERE, "Le token n'est pas correctement formé : " + token, e);
            throw e;
        }
    }
}