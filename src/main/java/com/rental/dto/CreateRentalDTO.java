package com.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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

    // Constructeurs
    public CreateRentalDTO() {}

    public CreateRentalDTO(String name, String description, double price, String location) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
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
}