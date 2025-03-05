package com.rental.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


/**
 * DTO pour la création d'une location.
 */
public class CreateRentalDTO {

    @NotBlank(message = "Le nom est obligatoire.")
    private String name;

    @NotNull(message = "La surface est obligatoire.")
    @Positive(message = "La surface doit être positive.")
    private int surface;

    @NotNull(message = "Le prix est obligatoire.")
    @Positive(message = "Le prix doit être positif.")
    private double price;

    // Stocke le chemin ou l'URL de l'image, non le fichier
    private String picturePath;

    @NotBlank(message = "La description est obligatoire.")
    private String description;

    @NotBlank(message = "L'ID du propriétaire est obligatoire.")
    private Long owner_id;

    @NotBlank(message = "La localisation est obligatoire.")
    private String location;

    // Constructeurs
    public CreateRentalDTO() {}

    public CreateRentalDTO(String name, String description, double price, String location, int surface, String picturePath, Long owner_id) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
        this.surface = surface;
        this.picturePath = picturePath;
        this.owner_id = owner_id;
    }


    // Getters et Setters
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

    public Long getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(Long owner_id) {
        this.owner_id = owner_id;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

}
