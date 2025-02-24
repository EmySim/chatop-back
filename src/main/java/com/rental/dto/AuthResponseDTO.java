package com.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) for authentication responses.
 */
public class AuthResponseDTO {

    @Schema(description = "Token JWT généré après authentification")
    private final String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
