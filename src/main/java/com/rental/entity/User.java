package com.rental.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entité représentant un utilisateur dans le système.
 * Gère les informations de base comme l'ID, le nom, l'e-mail, le rôle et les
 * timestamps.
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

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
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Rental> rentals;

    // ====== Constructeurs ======

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
     * @param email    Email de l'utilisateur.
     * @param name     Nom de l'utilisateur.
     * @param password Mot de passe.
     * @param role     Rôle de l'utilisateur.
     */
    public User(String email, String name, String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    /**
     * Constructeur prenant uniquement l'ID de l'utilisateur.
     * Utile pour les associations utilisant une clé étrangère.
     */
    public User(Long id) {
        this.id = id;
    }

    /**
     * Constructeur pour créer un utilisateur avec un rôle défini par défaut (USER).
     *
     * @param email    Email de l'utilisateur.
     * @param name     Nom de l'utilisateur.
     * @param password Mot de passe.
     */
    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = Role.USER;
    }

    // ====== Getters et Setters ======

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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    // ====== Représentation textuelle ======

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                ", role=" + role +
                '}';
    }
}