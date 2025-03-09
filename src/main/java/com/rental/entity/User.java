package com.rental.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import com.rental.dto.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Entité représentant un utilisateur dans le système.
 * Gère les informations de base comme l'ID, le nom, l'e-mail, le rôle et les timestamps.
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

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Rental> rentals;

    /**
     * Constructeur par défaut obligatoire pour Hibernate.
     * Attribue par défaut un rôle USER.
     */
    public User() {
        this.role = Role.USER;
    }

    /**
     * Constructeur pour créer un utilisateur avec un rôle spécifique.
     *
     * @param email Email de l'utilisateur.
     * @param name Nom de l'utilisateur.
     * @param password Mot de passe.
     * @param role Rôle de l'utilisateur.
     */
    public User(String email, String name, String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    /**
     * Constructeur pour créer un utilisateur avec un rôle défini par défaut (USER).
     *
     * @param email Email de l'utilisateur.
     * @param name Nom de l'utilisateur.
     * @param password Mot de passe.
     */
    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = Role.USER;
    }

    /**
     * Nouveau constructeur : Permet de créer un utilisateur avec uniquement un ID.
     *
     * Utile pour établir des relations (ex : associer un propriétaire à une location)
     * sans devoir récupérer toutes les données de l'utilisateur.
     *
     * @param id Identifiant unique de l'utilisateur.
     */
    public User(Long id) {
        this.id = id;
    }

    /**
     * Méthode exécutée lors de la création d'un utilisateur (persist).
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        logger.info("Création d'un nouvel utilisateur : " + this);
    }

    /**
     * Méthode exécutée lors de la mise à jour d'un utilisateur (update).
     */
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
        logger.info("Mise à jour de l'utilisateur : " + this);
    }

    // ======= Getters et setters =======

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
     * Méthode statique pour convertir une entité User en DTO.
     *
     * @param user Objet User à convertir.
     * @return Objet UserDTO représentant l'utilisateur.
     * @throws IllegalArgumentException si l'objet User est null.
     */
    public static UserDTO toUserDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null pour la conversion en UserDTO.");
        }
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    // ======= Représentation textuelle =======

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}