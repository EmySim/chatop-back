package com.rental.dto;

import com.rental.entity.Role;
import com.rental.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Transfer Object (DTO) pour les informations utilisateur.
 * Ce DTO est utilisé pour transférer les données d'un utilisateur
 * entre les couches de l'application.
 */
@Tag(name = "UserDTO", description = "Data Transfer Object pour les informations d'utilisateur")
public class UserDTO {

    private static final Logger logger = Logger.getLogger(UserDTO.class.getName()); // Logger pour les logs

    @Schema(description = "Identifiant unique de l'utilisateur")
    private Long id;

    @Schema(description = "Adresse email de l'utilisateur")
    private String email;

    @Schema(description = "Nom de l'utilisateur")
    private String name;

    @Schema(description = "Rôle de l'utilisateur")
    private Role role;

    /**
     * Constructeur par défaut (nécessaire pour la sérialisation).
     */
    public UserDTO() {
        logger.fine("Création d'un DTO utilisateur (constructeur par défaut).");
    }

    /**
     * Constructeur principal pour création de DTO.
     */
    public UserDTO(Long id, String email, String name, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;

        logger.log(Level.FINE, String.format(
                "Création d'un DTO utilisateur avec id=%d, email=%s, name=%s",
                id, email, name
        ));
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

            logger.log(Level.FINE, String.format(
                    "Création d'un DTO à partir de l'entité User : id=%d, email=%s",
                    user.getId(), user.getEmail()
            ));
        } else {
            logger.warning("Tentative de création de DTO à partir d'un utilisateur null.");
        }
    }

    /**
     * Méthode pour convertir ce DTO en une entité User.
     * (⚠️ Note : l'ID n'est pas réutilisé ici, attention en cas de mise à jour.)
     *
     * @return Un objet User basé sur ce DTO.
     */
    public User toEntity() {
        logger.log(Level.FINE, String.format(
                "Transformation du DTO en entité User pour email=%s",
                this.email
        ));

        return new User(this.email, this.name, null, this.role); // Mot de passe non inclus pour sécurité
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
}
