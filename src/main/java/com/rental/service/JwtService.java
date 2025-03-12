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

@Service
public class JwtService {

    private final Key signingKey;
    private final long jwtExpirationTime;

    // Constructeur pour initialiser la clé de signature et le temps d'expiration du JWT
    public JwtService(
            @Value("${JWT_SECRET}") String secretKeyBase64,
            @Value("${JWT_EXPIRATION}") long jwtExpirationTime) {

        if (secretKeyBase64 == null || secretKeyBase64.isEmpty()) {
            throw new IllegalArgumentException("La clé secrète JWT doit être définie.");
        }
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyBase64));
        this.jwtExpirationTime = jwtExpirationTime;
    }

    // Génère un token JWT pour un sujet donné
    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrait le nom d'utilisateur du token JWT
    public String extractUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    // Valide le token JWT en vérifiant le nom d'utilisateur et l'expiration
    public boolean validateToken(String token, String userDetails) {
        String username = extractUsername(token);
        return (username != null && username.equals(userDetails) && !isTokenExpired(token));
    }

    // Vérifie si le token JWT est expiré
    private boolean isTokenExpired(String token) {
        Date expiration = getClaim(token, Claims::getExpiration);
        return expiration != null && expiration.before(new Date());
    }

    // Récupère une claim spécifique du token JWT
    private <T> T getClaim(String token, Function<Claims, T> resolver) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
            return resolver.apply(claims);
        } catch (Exception e) {
            return null;  // Ou lever une exception custom
        }
    }
}