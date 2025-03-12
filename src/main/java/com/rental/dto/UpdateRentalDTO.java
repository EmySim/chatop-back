package com.rental.dto;

import java.util.Date;

/**
 * DTO pour mettre à jour une location existante.
 */
public class UpdateRentalDTO {

    private Long id;
    private String name;
    private int surface;
    private double price;
    private String description;
    private String picture;
    private Long ownerId;
    private Date createdAt;
    private Date updatedAt;

    // ====== Constructeurs ======

    /**
     * Constructeur sans arguments (par défaut).
     */
    public UpdateRentalDTO() {
        // Constructeur par défaut
    }

    /**
     * Constructeur avec tous les champs.
     *
     * @param id          L'identifiant unique de la location
     * @param name        Le nom de la location
     * @param description La description détaillée
     * @param price       Le prix de la location
     * @param surface     La surface en m²
     * @param picture     L'URL ou chemin de l'image associée
     * @param ownerId     ID du propriétaire (créateur)
     * @param createdAt   Date de création de la location
     * @param updatedAt   Date de dernière mise à jour de la location
     */
    public UpdateRentalDTO(Long id, String name, String description, double price, int surface, String picture,
            Long ownerId, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.surface = surface;
        this.picture = picture;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ====== Getters & Setters ======

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSurface() {
        return surface;
    }

    public void setSurface(int surface) {
        this.surface = surface;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ====== toString() ======

    @Override
    public String toString() {
        return "UpdateRentalDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surface=" + surface +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}