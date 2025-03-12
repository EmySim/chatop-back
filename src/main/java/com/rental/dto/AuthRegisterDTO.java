package com.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour l'inscription d'un nouvel utilisateur.
 */
public class AuthRegisterDTO {

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

    // Constructeur par défaut
    public AuthRegisterDTO() {
    }

    // Constructeur avec paramètres
    public AuthRegisterDTO(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

// ====== Getters & Setters ======
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}