package com.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) pour les réponses d'authentification.
 */
public class AuthResponseDTO {

    @Schema(description = "Token JWT généré après authentification")
    private final String token;

    /**
     * Constructeur pour initialiser le DTO avec un token.
     *
     * @param token le token JWT généré après authentification
     */
    public AuthResponseDTO(String token) {
        this.token = token;
    }

    /**
     * Retourne le token JWT.
     *
     * @return le token JWT
     */
    public String getToken() {
        return token;
    }
}