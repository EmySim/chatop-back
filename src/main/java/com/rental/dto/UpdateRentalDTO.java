package com.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.logging.Logger;

/**
 * DTO pour update d'une location existente.
 */
public class UpdateRentalDTO {

    private static final Logger logger = Logger.getLogger(UpdateRentalDTO.class.getName());

    // ID généré automatiquement lors de la création de la location
    private Long id;

    private String name;

    private int surface;

    private double price;

    private String picture;

    private String description;

    private Long ownerId;


    public UpdateRentalDTO(Long id, String name, String description, double price, int surface, String picture, Long ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.surface = surface;
        this.picture = picture;
        this.ownerId = ownerId;
    }
    // Getters and Setters
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

    public void setOwnerId(Long owner_id) {
        this.ownerId = owner_id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        logger.info("Début du stockage de l'URL de l'image.");
        this.picture = picture;
        logger.info("Fin du stockage de l'URL de l'image.");
    }
}
