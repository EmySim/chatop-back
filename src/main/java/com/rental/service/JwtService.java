package com.rental.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());

    private final SecretKey secretKey; // Nouvelle clé secrète utilisée pour sécuriser les tokens

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration; // Durée d'expiration injectée depuis application.properties

    /**
     * Constructeur qui initialise la clé secrète avec la valeur définie dans application.properties
     * (convertie en `SecretKey` pour supporter la nouvelle API).
     *
     * @param jwtSecret Clé secrète en base64 indiquée dans `application.properties`.
     */
    public JwtService(@Value("${JWT_SECRET}") String jwtSecret) {
        // Convertir la clé secrète string en format SecretKey requis par les nouvelles APIs
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Génère un token JWT pour un utilisateur donné.
     *
     * @param userDetails Les détails de l'utilisateur.
     * @return Le token JWT généré.
     */
    public String generateToken(UserDetails userDetails) {
        logger.info("Génération d'un token pour l'utilisateur : " + userDetails.getUsername());
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Le sujet est le "username" de l'utilisateur
                .claim("roles", userDetails.getAuthorities()) // Ajout des rôles en tant que claims
                .setIssuedAt(new Date()) // Date de génération du token
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Date d'expiration
                .signWith(secretKey) // Nouvelle façon de signer avec la SecretKey
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur (username) d'un token JWT.
     *
     * @param token Le token JWT reçu.
     * @return Username contenu dans le token.
     */
    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            logger.info("Nom d'utilisateur extrait depuis le token : " + username);
            return username;
        } catch (Exception e) {
            logger.severe("Erreur lors de l'extraction du nom d'utilisateur : " + e.getMessage());
            throw e;
        }
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
        try {
            String username = extractUsername(token); // Extraction du username
            boolean isTokenValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            logger.info("Validation du token pour l'utilisateur : " + username + " - Valide : " + isTokenValid);
            return isTokenValid;
        } catch (Exception e) {
            logger.severe("Erreur lors de la validation du token : " + e.getMessage());
            return false;
        }
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
        boolean expired = extractExpiration(token).before(new Date());
        logger.info("Le token est-il expiré ? " + expired);
        return expired;
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
        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(secretKey) // Nouvelle méthode recommandée pour passer la SecretKey
                    .build();
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (Exception e) {
            logger.severe("Erreur lors de l'extraction des claims : " + e.getMessage());
            throw e;
        }
    }
}