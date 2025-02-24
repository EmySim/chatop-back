package com.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) pour les requêtes d'authentification.
 */
public class AuthRequestDTO {

    @Schema(description = "Adresse email de l'utilisateur", example = "user@example.com")
    @Email(message = "L'email doit être valide.")
    @NotBlank(message = "L'email est obligatoire.")
    private String email;

    @Schema(description = "Mot de passe de l'utilisateur", example = "P@ssw0rd")
    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères.")
    private String password;

    @Schema(description = "Nom de l'utilisateur", example = "John Doe")
    @NotBlank(message = "Le nom est obligatoire.")
    private String name;

    // Constructeurs, Getters et Setters
    public AuthRequestDTO() {
    }

    public AuthRequestDTO(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}