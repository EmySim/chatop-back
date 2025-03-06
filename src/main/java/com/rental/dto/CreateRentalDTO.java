package com.rental.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.logging.Logger;

/**
 * DTO pour la création d'une location.
 */
public class CreateRentalDTO {

    private static final Logger logger = Logger.getLogger(CreateRentalDTO.class.getName());

    // ID généré automatiquement lors de la création de la location
    private Long id;

    @NotBlank(message = "Le nom est obligatoire.")
    private String name;

    @NotNull(message = "La surface est obligatoire.")
    @Positive(message = "La surface doit être positive.")
    private int surface;

    @NotNull(message = "Le prix est obligatoire.")
    @Positive(message = "Le prix doit être positif.")
    private double price;

    // Stocke le chemin ou l'URL de l'image, non le fichier
    private String pictureURL;

    @NotBlank(message = "La description est obligatoire.")
    private String description;

    @NotBlank(message = "L'ID du propriétaire est obligatoire.")
    private Long owner_id;

    @NotBlank(message = "La localisation est obligatoire.")
    private String location;

    // Constructeurs
    public CreateRentalDTO() {}

    public CreateRentalDTO(Long id, String name, String description, double price, String location, int surface, String pictureURL, Long owner_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
        this.surface = surface;
        this.pictureURL = pictureURL;
        this.owner_id = owner_id;
    }


    // Getters et Setters

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

    public String getpictureURL() {
        return pictureURL;
    }

    public void setpictureURL(String pictureURL) {
        logger.info("Début du stockage de l'URL de l'image.");
        this.pictureURL = pictureURL;
        logger.info("Fin du stockage de l'URL de l'image.");
    }

}
