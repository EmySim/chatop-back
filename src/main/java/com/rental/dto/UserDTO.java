package com.rental.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rental.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import com.rental.entity.User;

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

    @JsonProperty("created_at")
    @Schema(description = "Date de création du compte", example = "2025-02-28T14:23:45")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Dernière mise à jour du compte", example = "2025-02-28T14:23:45")
    private LocalDateTime lastUpdated;

    @Schema(description = "Rôle de l'utilisateur", example = "ADMIN")
    private Role role;

    @Schema(description = "Mot de passe de l'utilisateur (non exposé)")
    private String password;

    // Constructeur par défaut
    public UserDTO() {
    }

    /**
     * Constructeur avec rôle en `Role`
     */
    public UserDTO(Long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    /**
     * Constructeur prenant un objet User comme paramètre
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.lastUpdated = user.getLastUpdated();
    }

    /**
     * Constructeur avec tous les paramètres nécessaires pour initialiser l'objet.
     */
    public UserDTO(Long id, String name, String email, LocalDateTime createdAt, LocalDateTime lastUpdated, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
        this.role = role;
    }

    // Getters et Setters

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                ", role=" + role +
                '}';
    }
}