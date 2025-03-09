package com.rental.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.entity.User;
import com.rental.repository.RentalRepository;

/**
 * Service pour gérer les opérations liées aux locations.
 */
@Service
@Transactional
public class RentalService {

    private static final Logger logger = Logger.getLogger(RentalService.class.getName());

    private final RentalRepository rentalRepository;
    private final ImageStorageService imageStorageService;
    private final AuthService authService;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param rentalRepository Référentiel pour les entités Rental.
     * @param imageStorageService Service pour stocker les images.
     * @param authService Service pour gérer les authentifications.
     */
    @Autowired
    public RentalService(RentalRepository rentalRepository, ImageStorageService imageStorageService, AuthService authService) {
        this.rentalRepository = rentalRepository;
        this.imageStorageService = imageStorageService;
        this.authService = authService;
    }

    /**
     * Récupère toutes les locations.
     *
     * @return Liste de DTO représentant les locations.
     */
    public List<RentalDTO> getAllRentals() {
        logger.info("Récupération de toutes les locations.");
        List<Rental> rentals = rentalRepository.findAll();

        if (rentals.isEmpty()) {
            logger.warning("Aucune location trouvée.");
            return Collections.emptyList();
        }

        logger.info("Nombre de locations trouvées : " + rentals.size());
        return rentals.stream().map(this::mapToDTO).toList();
    }

    /**
     * Récupère une location par son ID.
     *
     * @param id L'identifiant de la location.
     * @return DTO représentant la location si trouvée.
     */
    public RentalDTO getRental(Long id) {
        logger.info("Récupération de la location avec l'ID : " + id);
        Rental rental = rentalRepository.findById(id).orElseThrow(() -> {
            logger.warning("Aucune location trouvée avec l'ID : " + id);
            return new IllegalArgumentException("Location introuvable avec l'ID fourni.");
        });

        return mapToDTO(rental);
    }

    /**
     * Enregistre une image via ImageStorageService et retourne son URL.
     *
     * @param image MultipartFile représentant l'image.
     * @return URL de l'image si l'opération réussit.
     */
    public Optional<String> saveImage(MultipartFile image) {
        logger.info("Tentative de sauvegarde de l'image.");
        if (image == null || image.isEmpty()) {
            logger.warning("Aucune image fournie pour la sauvegarde.");
            return Optional.empty();
        }

        return imageStorageService.saveImage(image);
    }

    /**
     * Crée une nouvelle location.
     *
     * @param createRentalDTO Données de création de la location.
     * @param picture Image associée à la location.
     * @param ownerId ID du propriétaire de la location.
     * @return DTO de la location créée.
     */
    public RentalDTO createRental(CreateRentalDTO createRentalDTO,
                                  MultipartFile picture, Long ownerId) {
        logger.info("Création d'une nouvelle location avec les données : " + createRentalDTO);

        // Transférer les données du DTO vers l'entité Rental
        Rental rental = new Rental();
        rental.setName(createRentalDTO.getName());
        rental.setDescription(createRentalDTO.getDescription());
        rental.setPrice(createRentalDTO.getPrice());
        rental.setSurface(createRentalDTO.getSurface());
        rental.setOwner(new User(ownerId));
        rental.setCreatedAt(new Date());
        rental.setUpdatedAt(new Date());


        // Sauvegarder l'image si elle est disponible
        if (picture != null && !picture.isEmpty()) {
            Optional<String> pictureURL = saveImage(picture);
            pictureURL.ifPresent(rental::setPicture); // Associer l'URL au champ `picture`
        } else {
            logger.warning("Aucune image n'a été fournie. Utilisation de l'image par défaut.");
            rental.setPicture("default_picture_url"); // Définir une valeur par défaut pour `picture`
        }
        // Enregistrer la location dans la base de données
        Rental savedRental = rentalRepository.save(rental);
        logger.info("Location créée avec succès : " + savedRental.getId());

        // Retourner le DTO
        return mapToDTO(savedRental);
    }

    /**
     * Met à jour une location existante.
     *
     * @param id L'identifiant de la location.
     * @param updateRentalDTO Les nouvelles données de la location.
     * @param picture Nouvelle image éventuelle de la location.
     * @return DTO avec les données mises à jour.
     */
    public RentalDTO updateRental(Long id, UpdateRentalDTO updateRentalDTO, MultipartFile picture) {
        logger.info("Mise à jour de la location avec l'ID : " + id);

        // Rechercher la location existante
        Rental rental = rentalRepository.findById(id).orElseThrow(() -> {
            logger.warning("Impossible de trouver la location avec l'ID : " + id);
            return new IllegalArgumentException("Location introuvable avec l'ID fourni.");
        });

        // Mettre à jour les champs
        rental.setName(updateRentalDTO.getName());
        rental.setDescription(updateRentalDTO.getDescription());
        rental.setPrice(updateRentalDTO.getPrice());
        rental.setSurface(updateRentalDTO.getSurface());
        rental.setUpdatedAt(new Date());

        // Mettre à jour l'image si elle existe
        if (picture != null && !picture.isEmpty()) {
            Optional<String> pictureURL = saveImage(picture);
            pictureURL.ifPresent(rental::setPicture); // Mise à jour de l'URL de l'image
        } else {
            logger.info("Aucune nouvelle image fournie, conservation de l'image existante.");
        }

        // Enregistrer les modifications
        Rental updatedRental = rentalRepository.save(rental);
        logger.info("Mise à jour réussie pour la location avec ID : " + updatedRental.getId());

        return mapToDTO(updatedRental);
    }

    /**
     * Transforme une entité Rental en DTO.
     *
     * @param rental Entité Rental à transformer.
     * @return DTO représentant la location.
     */
    private RentalDTO mapToDTO(Rental rental) {
        logger.info("Mapping de l'entité Rental vers le DTO pour l'ID : " + rental.getId());
        return new RentalDTO(
                rental.getId(),
                rental.getName(),
                rental.getDescription(),
                rental.getPrice(),
                rental.getSurface(),
                rental.getPicture(),
                rental.getCreatedAt(),
                rental.getUpdatedAt(),
                rental.getOwner().getId()
        );
    }
}