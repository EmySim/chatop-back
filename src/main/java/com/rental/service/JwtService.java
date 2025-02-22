package com.rental.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());

    private final Key secretKey;

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration;

    /**
     * Constructeur qui initialise la clé secrète.
     *
     * @param secretKeyBase64 Clé secrète en base64 injectée depuis application.properties.
     */
    public JwtService(@Value("${JWT_SECRET}") String secretKeyBase64) {
        this.secretKey = new SecretKeySpec(secretKeyBase64.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        logger.info("JwtService initialisé avec succès.");
    }

    /**
     * Génère un token JWT pour un utilisateur donné.
     *
     * @param userDetails Les détails de l'utilisateur.
     * @return Le token JWT généré.
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
     * Extrait le nom d'utilisateur (username) d'un token JWT.
     *
     * @param token Le token JWT reçu.
     * @return Username contenu dans le token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Valide le token JWT pour un utilisateur donné.
     * Cette méthode vérifie l'utilisateur et l'expiration du token.
     *
     * @param token       Le token JWT reçu.
     * @param userDetails Les détails de l'utilisateur attendu.
     * @return true si le token est valide, false sinon.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Extraction d'une information spécifique (claims) depuis un token JWT.
     *
     * @param token          Le token JWT.
     * @param claimsResolver Fonction pour résoudre un claim spécifique.
     * @param <T>            Type de la donnée extraite.
     * @return La donnée extraite du claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Vérifie si le token JWT est expiré.
     *
     * @param token Le token JWT.
     * @return true si le token est expiré, false sinon.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extraction de la date d'expiration d'un token JWT.
     *
     * @param token Le token JWT.
     * @return La date d'expiration extraite.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extraction de toutes les informations (claims) contenues dans un token JWT.
     *
     * @param token Le token JWT.
     * @return Les claims contenus dans le token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}