package com.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Date;
import java.util.logging.Logger;

/**
 * DTO pour mettre à jour une location existante.
 */
public class UpdateRentalDTO {

    private static final Logger logger = Logger.getLogger(UpdateRentalDTO.class.getName());

    // ID de la location
    @NotNull(message = "L'ID est requis pour mettre à jour une location.")
    private Long id;

    // Nom de la location
    @NotBlank(message = "Le nom de la location est requis.")
    private String name;

    // Surface de la location
    @Positive(message = "La surface doit être positive.")
    private int surface;

    // Prix de la location
    @Positive(message = "Le prix doit être positif.")
    private double price;

    // Description de la location
    private String description;

    // ID du propriétaire
    private Long ownerId;

    // Date de création de la location
    private Date createdAt;

    // Date de dernière mise à jour de la location
    private Date updatedAt;

    public UpdateRentalDTO() {
    }

    // Constructeur simplifié pour l'update
    public UpdateRentalDTO(Long id, String name, String description, double price, int surface, Long ownerId, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.surface = surface;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et setters
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
}
