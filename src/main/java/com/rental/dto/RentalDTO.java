package com.rental.dto;

import java.util.Date;

/**
 * DTO pour représenter une location (Rental).
 */
public class RentalDTO {
    private int id;
    private String name;
    private String description;
    private double price;
    private String location;
    private Date createdAt;
    private Date updatedAt;
    private int surface;
    private String picture;
    private Long owner_id;

    // Constructeur par défaut
    public RentalDTO() {
    }

    // Constructeur avec paramètres
    public RentalDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(Long owner_id) {
        this.owner_id = owner_id;
    }
}
