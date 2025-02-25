package com.rental.dto;

import com.rental.entity.Role;
import com.rental.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Data Transfer Object (DTO) pour les informations utilisateur.
 * Ce DTO est utilisé pour transférer les données d'un utilisateur
 * entre les couches de l'application.
 */
@Tag(name = "UserDTO", description = "Data Transfer Object pour les informations d'utilisateur")
public class UserDTO {

    private static final Logger logger = Logger.getLogger(UserDTO.class.getName());

    @Schema(description = "Identifiant unique de l'utilisateur")
    private Long id;

    @Schema(description = "Adresse email de l'utilisateur")
    private String email;

    @Schema(description = "Nom de l'utilisateur")
    private String name;

    @Schema(description = "Rôle de l'utilisateur")
    private Role role;

    @JsonProperty("created_at")
    @Schema(description = "Date de création de l'utilisateur")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Date de la dernière mise à jour de l'utilisateur")
    private LocalDateTime updatedAt;

    /**
     * Constructeur par défaut (nécessaire pour la sérialisation).
     */
    public UserDTO() {
        // logger.fine("Création d'un DTO utilisateur (constructeur par défaut).");
    }

    /**
     * Constructeur principal pour création de DTO.
     */
    public UserDTO(Long id, String email, String name, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        logger.info("UserDTO créé : " + this.toString());
    }

    /**
     * Nouveau constructeur avec moins de champs.
     */
    public UserDTO(Long id, String email, String name, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        logger.info("UserDTO partiellement créé : " + this.toString());
    }

    /**
     * Constructeur à partir d'un objet User (conversion entité -> DTO).
     */
    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.name = user.getName();
            this.role = user.getRole();
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();

            logger.info("UserDTO créé à partir d'un utilisateur : " + this.toString());
        }
    }

    /**
     * Méthode pour convertir ce DTO en une entité User.
     * (⚠️ Note : l'ID n'est pas réutilisé ici, attention en cas de mise à jour.)
     *
     * @return Un objet User basé sur ce DTO.
     */
    public User toEntity() {
        logger.info("Conversion de UserDTO en User entity : " + this.toString());
        User user = new User();
        user.setEmail(this.email);
        user.setName(this.name);
        user.setRole(this.role);
        return user;
    }

    /**
     * Vérifie si cet UserDTO est vide.
     * Un UserDTO est considéré comme vide si l'ID, l'email, le nom et le rôle ne sont pas définis.
     *
     * @return true si le DTO est vide, false sinon.
     */
    public boolean isEmpty() {
        boolean isEmptyData = (id == null || id == 0L)
                && (email == null || email.isEmpty())
                && (name == null || name.isEmpty())
                && (role == null)
                && (createdAt == null)
                && (updatedAt == null);

        logger.info("Vérification si UserDTO est vide : " + isEmptyData);
        return isEmptyData;
    }

    /**
     * Récupère la valeur d'un champ par son nom.
     *
     * @param fieldName Le nom du champ à récupérer.
     * @return La valeur du champ si elle existe, sinon une exception IllegalArgumentException.
     */
    public Object get(String fieldName) {
        switch (fieldName) {
            case "id":
                return id;
            case "email":
                return email;
            case "name":
                return name;
            case "role":
                return role;
            case "createdAt":
                return createdAt;
            case "updatedAt":
                return updatedAt;
            default:
                String error = "Champ invalide demandé dans UserDTO : " + fieldName;
                logger.warning(error);
                throw new IllegalArgumentException(error);
        }
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
