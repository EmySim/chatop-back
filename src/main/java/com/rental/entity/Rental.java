package com.rental.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Entité représentant une location.
 */
@Entity
@Table(name = "rentals")
public class Rental {

    private static final Logger logger = Logger.getLogger(Rental.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identifiant unique de la location

    @Column(nullable = false)
    private String name; // Nom de la location

    @Column(nullable = false)
    private String description; // Description détaillée de la location

    @Column(nullable = false)
    private double price; // Prix de la location

    @Column(nullable = false)
    private int surface; // Surface de la location en m²

    @Column(name = "picture", nullable = false)
    private String picture; // Colonne pour stocker l'URL de l'image associée à la location

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false) // Clé étrangère pour l'utilisateur propriétaire
    private User owner; // Propriétaire de la location (association avec User)

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt; // Date de création de l'entité

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt; // Date de dernière mise à jour de l'entité

    /**
     * Action à effectuer avant la persistance de l'entité (initialisation des dates).
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        logger.info("Création de l'entité Rental : " + this);
    }

    /**
     * Action à effectuer avant la mise à jour de l'entité (mise à jour de la date).
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
        logger.info("Mise à jour de l'entité Rental : " + this);
    }

    // ---- GETTERS ET SETTERS ---- //

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public int getSurface() { return surface; }

    public void setSurface(int surface) { this.surface = surface; }

    public String getPicture() { return picture; }

    /**
     * Met à jour l'URL de l'image associée à la location.
     *
     * @param picture URL de l'image
     */
    public void setPicture(String picture) {
        logger.info("Stockage de l'URL de l'image : " + picture);
        this.picture = picture;
    }

    public User getOwner() { return owner; }

    public void setOwner(User owner) { this.owner = owner; }

    public Date getCreatedAt() { return createdAt; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Permet d'appliquer une fonction sur l'entité et d'en retourner le résultat.
     *
     * @param mapper Fonction à appliquer sur l'entité
     * @return Résultat de l'application de la fonction
     */
    public <R> R map(Function<Rental, R> mapper) {
        return mapper.apply(this);
    }

    /**
     * Représentation de l'entité sous forme de chaîne de caractères.
     *
     * @return Chaîne de caractères représentant l'entité
     */
    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", surface=" + surface +
                ", picture='" + picture + '\'' +
                ", owner=" + (owner != null ? owner.getId() : "null") +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
