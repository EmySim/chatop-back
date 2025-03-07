package com.rental.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * DTO pour représenter une location (Rental).
 */
public class RentalDTO {
    private Long id;
    private String name;
    private int surface;
    private double price;
    private String pictureURL;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Long ownerId;

    // Constructeur sans argument
    public RentalDTO() {
    }

    // Constructeur avec arguments

    /// Constructeur
    public RentalDTO(Long id, String name, String description, double price, int surface, String pictureURL,
            Date createdAt, Date updatedAt, Long ownerId) {
        this.id = id;
        this.name = name;
        this.description = description; // Ajout de description
        this.price = price;
        this.surface = surface;
        this.pictureURL = pictureURL;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ownerId = ownerId;
    }

    // Getters et setters
    @JsonProperty("id") 
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

    public int getSurface() {
        return surface;
    }

    public void setSurface(int surface) {
        this.surface = surface;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    // Implémentation de toString()
    @Override
    public String toString() {
        return "RentalDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surface=" + surface +
                ", price=" + price +
                ", pictureURL='" + pictureURL + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", ownerId=" + ownerId +
                '}';
    }
}
