package com.rental.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.repository.RentalRepository;
import com.rental.entity.User;

@Service
@Transactional
public class RentalService {

    private static final Logger logger = Logger.getLogger(RentalService.class.getName());

    private final RentalRepository rentalRepository;
    private final ImageStorageService imageStorageService;
    private final AuthService authService;

    @Autowired
    public RentalService(RentalRepository rentalRepository, ImageStorageService imageStorageService, AuthService authService) {
        this.rentalRepository = rentalRepository;
        this.imageStorageService = imageStorageService;
        this.authService = authService;
    }

    /**
     * Récupère toutes les locations.
     * @return Liste de RentalDTO.
     */
    public List<RentalDTO> getAllRentals() {
        List<RentalDTO> rentals = rentalRepository.findAll().stream()
                .map(rental -> {
                    try {
                        return mapToDTO(rental);
                    } catch (Exception e) {
                        logger.warning("Erreur lors de la conversion d'une location en DTO : " + e.getMessage());
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .toList();

        logger.info("Nombre de locations trouvées : " + rentals.size());
        return rentals.isEmpty() ? Collections.emptyList() : rentals;
    }

    /**
     * Récupère une location par ID.
     * @param id L'identifiant de la location.
     * @return Un RentalDTO.
     */
    public RentalDTO getRental(Long id) {
        return rentalRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));
    }

    /**
     * Sauvegarde une image via ImageStorageService et retourne son chemin.
     * @param image MultipartFile de l'image.
     * @return Chemin de l'image stockée.
     */
    public Optional<String> saveImage(MultipartFile image) {
        logger.info("Début de l'appel à ImageStorageService pour sauvegarder l'image.");
        return imageStorageService.saveImage(image);
    }

    /**
     * Crée une nouvelle location.
     * @param createRentalDTO Données de création.
     * @param image L'image de la location.
     * @return DTO de la location créée.
     */
    public RentalDTO createRental(CreateRentalDTO createRentalDTO, MultipartFile image, Long ownerId) {
        logger.info("Début de la création d'une nouvelle location.");

        // Sauvegarder l'image
        String picture = null;
        if (image != null && !image.isEmpty()) {
            picture = imageStorageService.saveImage(image)
                    .orElseThrow(() -> new IllegalStateException("Erreur lors de l'enregistrement de l'image."));
            logger.info("Image sauvegardée avec succès : " + picture);
        }

        // Création de l'entité Rental
        Rental rental = new Rental();
        rental.setName(createRentalDTO.getName());
        rental.setDescription(createRentalDTO.getDescription());
        rental.setPrice(createRentalDTO.getPrice());
        rental.setPicture(picture); // Changement ici
        rental.setSurface(createRentalDTO.getSurface());
        rental.setCreatedAt(new Date());

        // Définir le propriétaire
        User authenticatedUser = authService.getAuthenticatedUser();
        rental.setOwner(authenticatedUser);

        // Sauvegarde en base
        Rental savedRental = rentalRepository.save(rental);
        logger.info("Location créée avec succès, ID: " + savedRental.getId());

        return mapToDTO(savedRental);
    }

    /**
     * Met à jour une location existante.
     * @param id L'identifiant de la location.
     * @param updateRentalDTO Les données mises à jour.
     * @param image L'image mise à jour.
     * @return Un RentalDTO avec les données mises à jour.
     */
    public RentalDTO updateRental(Long id, UpdateRentalDTO updateRentalDTO, MultipartFile image) {
        logger.info("Mise à jour de la location ID: " + id);

        Rental existingRental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));

        existingRental.setName(Optional.ofNullable(updateRentalDTO.getName()).orElse(existingRental.getName()));
        existingRental.setDescription(Optional.ofNullable(updateRentalDTO.getDescription()).orElse(existingRental.getDescription()));
        existingRental.setPrice(Optional.ofNullable(updateRentalDTO.getPrice()).orElse(existingRental.getPrice()));
        existingRental.setSurface(Optional.ofNullable(updateRentalDTO.getSurface()).orElse(existingRental.getSurface()));

        // Mise à jour de l'image si fournie
        if (image != null && !image.isEmpty()) {
            String imagePath = imageStorageService.saveImage(image)
                    .orElseThrow(() -> new RuntimeException("Échec de l'upload de l'image"));
            existingRental.setPicture(imagePath); // Changement ici
            logger.info("Image mise à jour pour la location ID: " + id);
        }

        existingRental.setUpdatedAt(new Date());
        rentalRepository.save(existingRental);
        logger.info("Location mise à jour avec succès, ID: " + id);

        return mapToDTO(existingRental);
    }

    /**
     * Transforme une entité Rental en DTO.
     */
    private RentalDTO mapToDTO(Rental rental) {
        logger.info("Conversion en DTO : " + rental.toString());
        return new RentalDTO(
                rental.getId(),
                rental.getName(),
                rental.getDescription(),
                rental.getPrice(),
                rental.getSurface(),
                rental.getPicture(), // Changement ici
                rental.getCreatedAt(),
                rental.getUpdatedAt(),
                rental.getOwner().getId()
        );
    }
}
