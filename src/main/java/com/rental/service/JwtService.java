package com.rental.service;

import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.function.Function;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());
    private final Key signingKey;
    private final long jwtExpirationTime;

    public JwtService(
            @Value("${JWT_SECRET}") String secretKeyBase64,
            @Value("${JWT_EXPIRATION}") long jwtExpirationTime) {

        if (secretKeyBase64 == null || secretKeyBase64.isEmpty()) {
            logger.severe("❌ La clé secrète JWT est manquante !");
            throw new IllegalArgumentException("La clé secrète JWT doit être définie.");
        }
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyBase64));
        this.jwtExpirationTime = jwtExpirationTime;
        logger.info("✅ Clé secrète JWT chargée avec expiration de " + jwtExpirationTime + " ms.");
    }

    public String generateToken(String subject) {
        logger.info("🛠️ Génération du token JWT pour : " + subject);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        logger.info("🔍 Extraction du username du token.");
        return getClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, String userDetails) {
        String username = extractUsername(token);
        boolean isValid = (username != null && username.equals(userDetails) && !isTokenExpired(token));
        logger.info("✅ Validation du token : " + (isValid ? "VALIDE" : "INVALIDE"));
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getClaim(token, Claims::getExpiration);
        return expiration != null && expiration.before(new Date());
    }

    private <T> T getClaim(String token, Function<Claims, T> resolver) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
            return resolver.apply(claims);
        } catch (Exception e) {
            logger.severe("❌ Erreur lors de l'extraction des claims du token : " + e.getMessage());
            return null;  // Ou lever une exception custom
        }
    }
}
