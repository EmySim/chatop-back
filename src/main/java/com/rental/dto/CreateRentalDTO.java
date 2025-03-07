package com.rental.dto;

import java.util.logging.Logger;

/**
 * DTO pour la création d'une location.
 */
public class CreateRentalDTO {

    private static final Logger logger = Logger.getLogger(CreateRentalDTO.class.getName());


    private Long id;

    private String name;

    private int surface;

    private double price;

    // Stocke le chemin ou l'URL de l'image, non le fichier
    private String pictureURL;

    private String description;

    private Long ownerId;

    // Constructeurs
    public CreateRentalDTO() {}

    public CreateRentalDTO(Long id, String name, String description, double price, int surface, String pictureURL, Long ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.surface = surface;
        this.pictureURL = pictureURL;
        this.ownerId = ownerId;
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

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        logger.info("Début du stockage de l'URL de l'image.");
        this.pictureURL = pictureURL;
        logger.info("Fin du stockage de l'URL de l'image.");
    }

}
