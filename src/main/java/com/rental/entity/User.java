package com.rental.entity;

import java.time.LocalDateTime;

import com.rental.dto.UserDTO;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

import java.util.logging.Logger;

/**
 * Entité représentant un utilisateur dans le système.
 */
@Entity
@Table(name = "users")
public class User {

    private static final Logger logger = Logger.getLogger(User.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le nom ne peut pas être nul")
    @Size(min = 2, max = 50, message = "Le nom doit avoir entre 2 et 50 caractères")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "L'email ne peut pas être nul")
    @Email(message = "Veuillez fournir une adresse email valide")
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull(message = "Le mot de passe ne peut pas être nul")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Column(nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Constructeur par défaut (nécessaire pour Hibernate).
     */
    public User() {
        this.role = Role.USER;
    }

    /**
     * Constructeur pour créer un utilisateur avec un rôle spécifique.
     */
    public User(String email, String name, String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Rental> rentals;

    /**
     * Constructeur avec rôle par défaut (USER).
     */
    public User(String email, String name, String password) {

        this(email, name, password, Role.USER);
    }

    // Auto-setting des timestamps pour la persistance en base de données
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Initialise la date de création à la date actuelle
        this.lastUpdated = LocalDateTime.now(); // Initialise également la date de mise à jour
        logger.info("Utilisateur créé à : " + createdAt);
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
        logger.info("Utilisateur mis à jour à : " + lastUpdated);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Méthode statique pour transformer un objet User en UserDTO.
     * @param user L'utilisateur à transformer.
     * @return Un objet UserDTO.
     * @throws IllegalArgumentException si l'utilisateur est null
     */
    public static UserDTO toUserDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        // Retourne un UserDTO avec tous les champs nécessaires, y compris le rôle
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getLastUpdated(),
                user.getRole().name()
        );

    }
}