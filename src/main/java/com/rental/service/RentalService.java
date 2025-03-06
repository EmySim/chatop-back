package com.rental.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.repository.RentalRepository;

@Service
public class RentalService {

    private static final Logger logger = Logger.getLogger(RentalService.class.getName());
    private final RentalRepository rentalRepository;
    private final ImageStorageService imageStorageService;

    @Autowired
    public RentalService(RentalRepository rentalRepository, ImageStorageService imageStorageService) {
        this.rentalRepository = rentalRepository;
        this.imageStorageService = imageStorageService;
    }

    /**
     * Récupère toutes les locations.
     *
     * @return Liste de RentalDTO.
     */
    public List<RentalDTO> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Récupère une location par ID.
     *
     * @param id L'identifiant de la location.
     * @return Un RentalDTO ou une erreur si non trouvé.
     */
    public RentalDTO getRental(Long id) {
        return rentalRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));
    }

    /**
     * Sauvegarde une image via ImageStorageService et retourne son URL.
     *
     * @param image MultipartFile de l'image.
     * @return URL de l'image stockée.
     */
    public Optional<String> saveImage(MultipartFile image) {
        logger.info("Début de l'appel à ImageStorageService pour sauvegarder l'image.");
        return imageStorageService.saveImage(image);
    }


    /**
     * Crée une nouvelle location.
     *
     * @param createRentalDTO Données de création.
     * @param image           L'image de la location.
     * @param ownerId         L'identifiant du propriétaire.
     * @return DTO de la location créée.
     */
    public RentalDTO createRental(CreateRentalDTO createRentalDTO, MultipartFile image, Long ownerId) {
        logger.info("Début de la création d'une location"+ createRentalDTO.getId());


        // Sauvegarder l'image
        String pictureURL = null;
        if (image != null && !image.isEmpty()) {
            pictureURL = imageStorageService.saveImage(image)
                    .orElseThrow(() -> new IllegalStateException("Une erreur est survenue lors de l'enregistrement de l'image."));
            logger.info("Image sauvegardée avec succès : " + pictureURL);
        }

        // Création de l'entité Rental
        Rental rental = new Rental();
        rental.setId(createRentalDTO.getId());
        rental.setName(createRentalDTO.getName());
        rental.setDescription(createRentalDTO.getDescription());
        rental.setPrice(createRentalDTO.getPrice());
        rental.setpictureURL(pictureURL);
        rental.setLocation(createRentalDTO.getLocation());
        rental.setOwnerId(ownerId);
        rental.setSurface(createRentalDTO.getSurface());
        rental.setCreatedAt(new Date());

        // Sauvegarde en base
        Rental savedRental = rentalRepository.save(rental);
        logger.info("Location créée avec succès, ID: " + rental.getId());

        return mapToDTO(savedRental);
    }

    /**
     * Met à jour une location existante.
     *
     * @param id              L'identifiant de la location.
     * @param updateRentalDTO Les données mises à jour.
     * @param image           L'image mise à jour.
     * @return Un RentalDTO avec les données mises à jour.
     */
    public RentalDTO updateRental(Long id, UpdateRentalDTO updateRentalDTO, MultipartFile image) {
        Rental existingRental = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));

        existingRental.setName(Optional.ofNullable(updateRentalDTO.getName()).orElse(existingRental.getName()));
        existingRental.setDescription(Optional.ofNullable(updateRentalDTO.getDescription()).orElse(existingRental.getDescription()));
        existingRental.setPrice(Optional.ofNullable(updateRentalDTO.getPrice()).orElse(existingRental.getPrice()));
        existingRental.setLocation(Optional.ofNullable(updateRentalDTO.getLocation()).orElse(existingRental.getLocation()));
        existingRental.setSurface(Optional.ofNullable(updateRentalDTO.getSurface()).orElse(existingRental.getSurface()));

        // Mise à jour de l'image si fournie
        if (image != null && !image.isEmpty()) {
            String imagePath = imageStorageService.saveImage(image)
                    .orElseThrow(() -> new RuntimeException("Échec de l'upload de l'image"));
            existingRental.setpictureURL(imagePath);
        }

        existingRental.setUpdatedAt(new Date());
        rentalRepository.save(existingRental);

        return mapToDTO(existingRental);
    }

    /**
     * Transforme une entité Rental en DTO.
     */
    private RentalDTO mapToDTO(Rental rental) {
        RentalDTO dto = new RentalDTO();
        dto.setId(rental.getId());
        dto.setName(rental.getName());
        dto.setDescription(rental.getDescription());
        dto.setPrice(rental.getPrice());
        dto.setLocation(rental.getLocation());
        dto.setCreatedAt(rental.getCreatedAt());
        dto.setUpdatedAt(rental.getUpdatedAt());
        dto.setSurface(rental.getSurface());
        dto.setpictureURL(rental.getpictureURL());
        dto.setOwnerId(rental.getOwnerId());
        return dto;
    }
}
