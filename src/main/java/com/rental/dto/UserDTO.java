package com.rental.dto;

import com.rental.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Transfer Object (DTO) pour les informations utilisateur.
 * Ce DTO est utilisé pour transférer les données d'un utilisateur
 * entre les couches de l'application.
 */
@Tag(name = "UserDTO", description = "Data Transfer Object pour les informations d'utilisateur")
public class UserDTO {

    private static final Logger logger = LoggerFactory.getLogger(UserDTO.class); // Logger pour les logs

    @Schema(description = "Identifiant unique de l'utilisateur", example = "1")
    private Long id;  // Ajout de l'id de l'utilisateur

    @Schema(description = "Adresse email de l'utilisateur", example = "user@example.com")
    private String email;

    @Schema(description = "Nom de l'utilisateur", example = "John Doe")
    private String name;

    @Schema(description = "Mot de passe de l'utilisateur (en clair, à chiffrer avant stockage)", example = "password123")
    private String password;

    // Constructeur par défaut
    public UserDTO() {
        logger.debug("Création d'un DTO utilisateur avec le constructeur par défaut.");
    }

    // Constructeur principal avec les paramètres
    public UserDTO(Long id, String email, String name, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        logger.debug("Création d'un DTO utilisateur avec les paramètres : id={}, email={}, name={}", id, email, name);
    }

    // Nouveau constructeur avec trois paramètres (sans mot de passe)
    public UserDTO(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        logger.debug("Création d'un DTO utilisateur avec les paramètres : id={}, email={}, name={}", id, email, name);
    }

    // Constructeur avec trois arguments (email, name, password)
    public UserDTO(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        logger.debug("Création d'un DTO utilisateur avec email={}, name={}, password={}", email, name, password);
    }

    // Constructeur avec un objet User
    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.name = user.getName();
            this.password = user.getPassword();
            logger.debug("Création d'un DTO à partir de l'entité User : id={}, email={}, name={}", user.getId(), user.getEmail(), user.getName());
        } else {
            logger.warn("Tentative de création de DTO à partir d'un utilisateur null.");
        }
    }

    /**
     * Méthode pour transformer ce DTO en une entité User.
     * Cette méthode est utilisée pour convertir le DTO en une entité avant de la persister.
     *
     * @return l'entité User correspondante
     */
    public User toEntity() {
        logger.debug("Transformation du DTO en entité User avec email={}, name={}", this.email, this.name);
        return new User(this.email, this.name, this.password);  // Assumer que User a un constructeur avec id
    }

    // Getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        logger.debug("Modification de l'id de l'utilisateur : id={}", id);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        logger.debug("Modification de l'email de l'utilisateur : email={}", email);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        logger.debug("Modification du nom de l'utilisateur : name={}", name);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        logger.debug("Modification du mot de passe de l'utilisateur.");
    }
}
