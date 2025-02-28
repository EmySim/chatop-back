package com.rental.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO représentant un utilisateur pour l'exposition via API.
 * Cette classe permet de transférer les informations de l'utilisateur entre les couches de l'application.
 */
@Schema(description = "Représentation d'un utilisateur")
public class UserDTO {

    @Schema(description = "Identifiant unique de l'utilisateur", example = "1")
    private Long id;

    @Schema(description = "Nom de l'utilisateur", example = "John Doe")
    private String name;

    @Schema(description = "Email de l'utilisateur", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Date de création du compte", example = "2025-02-28T14:23:45")
    private LocalDateTime createdAt;

    @Schema(description = "Dernière mise à jour du compte", example = "2025-02-28T14:23:45")
    private LocalDateTime lastUpdated;

    @Schema(description = "Rôle de l'utilisateur", example = "ADMIN")
    private String role; // Déclaration de la propriété "role"

    // Constructeur par défaut nécessaire pour la désérialisation des données depuis JSON
    public UserDTO() {}

    /**
     * Constructeur avec tous les paramètres nécessaires pour initialiser l'objet.
     * @param id L'ID de l'utilisateur
     * @param name Le nom de l'utilisateur
     * @param email L'email de l'utilisateur
     * @param createdAt La date de création de l'utilisateur
     * @param lastUpdated La date de la dernière mise à jour de l'utilisateur
     * @param role Le rôle de l'utilisateur
     */
    public UserDTO(Long id, String name, String email, LocalDateTime createdAt, LocalDateTime lastUpdated, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
        this.role = role; // Initialisation du rôle
    }

    // Getters et Setters avec annotations Swagger pour la documentation automatique

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role; // Retourne la valeur du rôle
    }

    public void setRole(String role) {
        this.role = role; // Définit la valeur du rôle
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                ", role='" + role + '\'' + // Ajout du rôle dans le toString
                '}';
    }
}