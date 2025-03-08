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
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private int surface;

    @Column(name = "picture_url", nullable = false)
    private String pictureURL;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        logger.info("Création de l'entité Rental : " + this);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
        logger.info("Mise à jour de l'entité Rental : " + this);
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getSurface() { return surface; }
    public void setSurface(int surface) { this.surface = surface; }

    public String getPictureURL() { return pictureURL; }
    public void setPictureURL(String pictureURL) {
        logger.info("Stockage de l'URL de l'image : " + pictureURL);
        this.pictureURL = pictureURL;
    }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public <R> R map(Function<Rental, R> mapper) {
        return mapper.apply(this);
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", surface=" + surface +
                ", pictureURL='" + pictureURL + '\'' +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
