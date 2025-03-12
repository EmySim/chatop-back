package com.rental.dto;

import java.util.Date;
import java.util.Objects; // Ajouté si des opérations de comparaison/matching sont nécessaires
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO pour la création d'une location.
 * Ce DTO est utilisé pour transférer les données lors de la création d'une location via l'API.
 */
public class CreateRentalDTO {

    // ID de la location (optionnel)
    private Long id;

    // Nom de la location
    private String name;

    // Surface de la location (en m²)
    private int surface;

    // Prix de la location
    private double price;

    // Fichier associé (au lieu de conserver un chemin ou URL directement)
    private MultipartFile picture;

    // La description de la location
    private String description;

    // ID du propriétaire ou utilisateur créateur de la location
    private Long ownerId;

    // Date de création de la location
    private Date createdAt;

    // Date de dernière mise à jour de la location
    private Date updatedAt;

    // ====== Constructeurs ======

    /**
     * Constructeur sans arguments (par défaut).
     */
    public CreateRentalDTO() {}

    /**
     * Constructeur avec tous les champs.
     *
     * @param id L'identifiant unique de la location
     * @param name Le nom de la location
     * @param description La description détaillée
     * @param price Le prix de la location
     * @param surface La surface en m²
     * @param picture Le fichier associé (image de la location)
     * @param ownerId ID du propriétaire (créateur)
     * @param createdAt Date de création de la location
     * @param updatedAt Date de dernière mise à jour de la location
     */
    public CreateRentalDTO(Long id, String name, String description, double price, int surface, MultipartFile picture, Long ownerId, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.surface = surface;
        this.picture = picture;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ====== Getters & Setters ======

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

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
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

    @Override
    public String toString() {
        return "CreateRentalDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surface=" + surface +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateRentalDTO that = (CreateRentalDTO) o;
        return surface == that.surface &&
                Double.compare(that.price, price) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(picture, that.picture) &&
                Objects.equals(description, that.description) &&
                Objects.equals(ownerId, that.ownerId) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surface, price, picture, description, ownerId, createdAt, updatedAt);
    }
}