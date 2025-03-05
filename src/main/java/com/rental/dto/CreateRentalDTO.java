package com.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Base64;

/**
 * DTO for creating a new rental.
 */
public class CreateRentalDTO {

    @NotBlank(message = "Le nom est obligatoire.")
    private String name;

    @NotBlank(message = "La description est obligatoire.")
    private String description;

    @NotNull(message = "Le prix est obligatoire.")
    @Positive(message = "Le prix doit être positif.")
    private double price;

    @NotBlank(message = "La localisation est obligatoire.")
    private String location;

    @NotNull(message = "La surface est obligatoire.")
    @Positive(message = "La surface doit être positive.")
    private int surface;

    @NotBlank(message = "L'image est obligatoire.")
    private String picture;

    @NotNull(message = "L'ID du propriétaire est obligatoire.")
    private Long owner_id;

    public Long getOwnerId() {
        return owner_id; // Correction pour utiliser la propriété correcte
    }

    // Constructeurs
    public CreateRentalDTO() {}

    public CreateRentalDTO(String name, String description, double price, String location, int surface, String picture, Long owner_id) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
        this.surface = surface;
        this.picture = picture;
        this.owner_id = owner_id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    // Add base64Url encoding for the rental body
    public String getEncodedDescription() {
        return Base64.getUrlEncoder().encodeToString(description.getBytes());
    }

    // Add base64Url decoding for the rental body
    public void setEncodedDescription(String encodedDescription) {
        this.description = new String(Base64.getUrlDecoder().decode(encodedDescription));
    }
}
