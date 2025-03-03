package com.rental.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.function.Function;

/**
 * Entity class representing a rental property.
 */
@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Génération automatique de l'ID
    private Long id;

    @Column(nullable = false) // Nom requis non null
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private int surface;

    @Column(name = "picture", nullable = false) // Synchronisation avec la colonne existante "picture"
    private String pictureUrl;

    @Column(nullable = false)
    private Long owner_id;

    @Temporal(TemporalType.TIMESTAMP) // Pour les champs de type Date
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getSurface() { return surface; }
    public void setSurface(int surface) { this.surface = surface; }

    public String getPicture() { return pictureUrl; }
    public void setPicture(String picture) { this.pictureUrl = picture; }

    public Long getOwner_id() { return owner_id; }
    public void setOwner_id(Long owner_id) { this.owner_id = owner_id; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public <R> R map(Function<Rental, R> mapper) {
        return mapper.apply(this);
    }
}
