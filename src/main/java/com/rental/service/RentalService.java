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

import io.jsonwebtoken.io.IOException;

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
     * Récupère toutes les locations dans une liste.
     * 
     * @return Liste de RentalDTO
     */
    public List<RentalDTO> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Récupère une location par ID.
     * 
     * @param id L'identifiant de la location
     * @return Un RentalDTO ou une erreur si non trouvé
     */
    public RentalDTO getRental(Long id) {
        return rentalRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));
    }

    /**
     * Stocke l'image et retourne son chemin ou URL.
     *
     * @param image Le fichier image.
     * @return Chemin ou URL de l'image sauvegardée.
     */
    public String storeImage(MultipartFile image) {
        try {
            return imageStorageService.saveImage(image)
                    .orElseThrow(() -> new RuntimeException("Échec de l'upload de l'image"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la sauvegarde de l'image", e);
            throw new RuntimeException("Impossible de sauvegarder l'image");
        }
    }

    /**
     * Crée une nouvelle location.
     *
     * @param createRentalDTO Données de création.
     * @param image           L'image de la location.
     * @return DTO de la location créée.
     */
    public RentalDTO createRental(CreateRentalDTO createRentalDTO, MultipartFile image) throws IOException {
        Rental rental = new Rental();
        rental.setName(createRentalDTO.getName());
        rental.setDescription(createRentalDTO.getDescription());
        rental.setPrice(createRentalDTO.getPrice());
        rental.setLocation(createRentalDTO.getLocation());
        rental.setSurface(createRentalDTO.getSurface());
        rental.setOwnerId(createRentalDTO.getOwnerId());

        // Gestion de l'image
        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            imagePath = storeImage(image); // Méthode qui gère le stockage de l'image
        }
        rental.setPicturePath(imagePath); // Assurez-vous d'utiliser `setPicturePath` et pas `setPicture`

        // Sauvegarder la location
        rentalRepository.save(rental);

        // Retourner le DTO de la location créée
        return mapToDTO(rental);
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
        existingRental.setDescription(
                Optional.ofNullable(updateRentalDTO.getDescription()).orElse(existingRental.getDescription()));
        existingRental.setPrice(Optional.of(updateRentalDTO.getPrice()).orElse(existingRental.getPrice()));
        existingRental
                .setLocation(Optional.ofNullable(updateRentalDTO.getLocation()).orElse(existingRental.getLocation()));
        existingRental.setSurface(Optional.ofNullable(updateRentalDTO.getSurface()).orElse(existingRental.getSurface()));

        // Mise à jour de l'image si nécessaire
        if (image != null && !image.isEmpty()) {
            String imagePath = storeImage(image);
            existingRental.setPicturePath(imagePath); // Correctement définir le chemin de l'image
        }

        existingRental.setUpdatedAt(new Date());
        rentalRepository.save(existingRental);
        return mapToDTO(existingRental);
    }

    /**
     * Transforme une entité Rental en DTO.
     * 
     * @param rental L'entité Rental
     * @return Le DTO correspondant
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
        dto.setPicturePath(rental.getPicturePath()); // Assurez-vous d'utiliser `getPicturePath`
        dto.setOwnerId(rental.getOwnerId());
        return dto;
    }
}
