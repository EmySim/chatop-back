package com.rental.service;

import com.rental.dto.RentalDTO;
import com.rental.dto.CreateRentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * @return Liste de RentalDTO
     */
    public List<RentalDTO> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Récupère une location par ID.
     * @param id L'identifiant de la location
     * @return Un RentalDTO ou une erreur si non trouvé
     */
    public RentalDTO getRental(Long id) {
        return rentalRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));
    }

    /**
     * Crée une nouvelle location.
     * @param createRentalDTO Données de création
     * @param image Image optionnelle
     * @return Un RentalDTO
     */
    public RentalDTO createRental(CreateRentalDTO createRentalDTO, MultipartFile image) {
        Rental rental = new Rental();
        rental.setName(createRentalDTO.getName());
        rental.setDescription(createRentalDTO.getDescription());
        rental.setPrice(createRentalDTO.getPrice());
        rental.setLocation(createRentalDTO.getLocation());
        rental.setOwnerId(createRentalDTO.getOwnerId());
        rental.setSurface(createRentalDTO.getSurface());

        if (image != null) {
            String url = imageStorageService.storeImage(image);
            rental.setPicture(url);
        }

        rental = rentalRepository.save(rental);
        return mapToDTO(rental);
    }

    /**
     * Met à jour une location existante.
     * @param id L'identifiant de la location.
     * @param updateRentalDTO Les données mises à jour.
     * @param image L'image mise à jour.
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

        if (image != null) {
            String url = imageStorageService.storeImage(image);
            existingRental.setPicture(url);
        }

        existingRental = rentalRepository.save(existingRental);
        return mapToDTO(existingRental);
    }

    /**
     * Transforme une entité Rental en DTO.
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
        dto.setPicture(rental.getPicture());
        dto.setOwnerId(rental.getOwnerId());
        return dto;
    }

    // Implement base64Url encoding for the rental body
    public String encodeRentalBody(String rentalBody) {
        return Base64.getUrlEncoder().encodeToString(rentalBody.getBytes());
    }

    // Implement base64Url decoding for the rental body
    public String decodeRentalBody(String encodedRentalBody) {
        return new String(Base64.getUrlDecoder().decode(encodedRentalBody));
    }
}