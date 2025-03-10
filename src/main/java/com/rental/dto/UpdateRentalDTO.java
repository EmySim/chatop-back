package com.rental.dto;

import java.util.Date;
import java.util.logging.Logger;

/**
 * DTO pour mettre à jour une location existante.
 */
public class UpdateRentalDTO {

    // Logger pour enregistrer les opérations liées à CreateRentalDTO
    private static final Logger logger = Logger.getLogger(CreateRentalDTO.class.getName());

    // ID de la location (optionnel)
    private Long id;

    // Nom de la location
    private String name;

    // Surface de la location (en m²)
    private int surface;

    // Prix de la location
    private double price;

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
    public UpdateRentalDTO() {
        logger.info("Instanciation d'une MAJ objet UpdateRentalDTO (sans paramètres).");
    }

    /**
     * Constructeur avec tous les champs.
     *
     * @param id L'identifiant unique de la location
     * @param name Le nom de la location
     * @param description La description détaillée
     * @param price Le prix de la location
     * @param surface La surface en m²
     * @param picture L'URL ou chemin de l'image associée
     * @param ownerId ID du propriétaire (créateur)
     * @param createdAt Date de création de la location
     * @param updatedAt Date de dernière mise à jour de la location
     */
    public UpdateRentalDTO(Long id, String name, String description, double price, int surface, String picture, Long ownerId, Date createdAt, Date updatedAt) {
        logger.info("Instanciation d'une MAJ objet UpdateRentalDTO avec tous les champs.");
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.surface = surface;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ====== Getters & Setters ======

    /**
     * Récupère l'ID de la location.
     *
     * @return Long L'identifiant unique
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l'ID de la location.
     *
     * @param id L'identifiant unique
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Récupère le nom de la location.
     *
     * @return String Le nom de la location
     */
    public String getName() {
        return name;
    }

    /**
     * Définit le nom de la location.
     *
     * @param name Le nom de la location
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Récupère la description de la location.
     *
     * @return String La description détaillée
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la description de la location.
     *
     * @param description La description détaillée
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Récupère le prix de la location.
     *
     * @return double Le prix en devise monétaire
     */
    public double getPrice() {
        return price;
    }

    /**
     * Définit le prix de la location.
     *
     * @param price Le prix en devise monétaire
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Récupère la surface (en m²) de la location.
     *
     * @return int La superficie de la location
     */
    public int getSurface() {
        return surface;
    }

    /**
     * Définit la surface (en m²) de la location.
     *
     * @param surface La superficie de la location
     */
    public void setSurface(int surface) {
        this.surface = surface;
    }

    /**
     * Récupère l'ID du propriétaire.
     *
     * @return Long L'identifiant unique du propriétaire
     */
    public Long getOwnerId() {
        return ownerId;
    }

    /**
     * Définit l'ID du propriétaire.
     *
     * @param ownerId L'identifiant unique du propriétaire
     */
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }


    /**
     * Récupère la date de dernière mise à jour de la location.
     *
     * @return Date La date de dernière mise à jour
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Définit la date de dernière mise à jour de la location.
     *
     * @param updatedAt La date de dernière mise à jour
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ====== toString() ======

    /**
     * Représentation sous forme de chaîne pour les logs/débogage.
     *
     * @return String Représentation détaillée de l'objet
     */
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

}
