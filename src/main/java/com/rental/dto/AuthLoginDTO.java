package com.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la connexion d'un utilisateur.
 */
public class AuthLoginDTO {


    @Schema(description = "Identifiant (adresse email) de l'utilisateur", example = "user@example.com")
    @Email(message = "L'identifiant doit être une adresse email valide.")
    @NotBlank(message = "L'identifiant est obligatoire.")
    private String email;


    @Schema(description = "Mot de passe de l'utilisateur", example = "P@ssw0rd")
    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères.")
    private String password;

    // Constructeurs, Getters & Setters
    public AuthLoginDTO() {}

    public AuthLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}