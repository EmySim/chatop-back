package com.rental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages") // Mappe la table "messages" MySQL
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation
    private Long id;

    @Column(name = "rental_id", nullable = false) // Mappe "rental_id" dans le JSON / BDD
    private Long rentalId;

    @Column(name = "user_id") // Mappe "user_id" dans le JSON / BDD
    private Long userId;

    @Column(name = "message", nullable = false, length = 2000) // Mappe "message" dans le JSON / BDD
    private String message;

    @Column(name = "created_at", updatable = false) // Date de création
    private LocalDateTime createdAt;

    @Column(name = "updated_at") // Date de mise à jour
    private LocalDateTime updatedAt;

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}