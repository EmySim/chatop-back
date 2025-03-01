package com.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for creating a new rental.
 */
public class CreateRentalDTO {

    // Constructeurs
    public CreateRentalDTO(String propertyName) {
        this.propertyName = propertyName;
    }


    @NotBlank(message = "Le nom est obligatoire.")
    private String name;

    @NotBlank(message = "La description est obligatoire.")
    private String description;

    @NotNull(message = "Le prix est obligatoire.")
    @Positive(message = "Le prix doit être positif.")
    private double price;

    @NotBlank(message = "La localisation est obligatoire.")
    private String location;

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
}
