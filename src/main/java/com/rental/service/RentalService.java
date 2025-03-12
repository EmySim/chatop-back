package com.rental.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
 * Service métier pour la gestion des locations.
 */
@Service
@Transactional
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ImageStorageService imageStorageService;

    public RentalService(RentalRepository rentalRepository, ImageStorageService imageStorageService) {
        this.rentalRepository = rentalRepository;
        this.imageStorageService = imageStorageService;
    }

    /**
     * Récupère toutes les locations.
     * @return Liste de RentalDTO
     */
    public List<RentalDTO> getAllRentals() {
        return rentalRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une location par son ID.
     * @param id ID de la location
     * @return RentalDTO de la location
     */
    public RentalDTO getRental(Long id) {
        return rentalRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new NoSuchElementException("Location non trouvée avec ID : " + id));
    }

    /**
     * Crée une nouvelle location.
     * @param createRentalDTO DTO contenant les informations de la location
     * @param picture Image de la location
     * @param ownerId ID du propriétaire
     * @return RentalDTO de la location créée
     */
    public RentalDTO createRental(CreateRentalDTO createRentalDTO, MultipartFile picture, Long ownerId) {
        Rental rental = new Rental();
        rental.setName(createRentalDTO.getName());
        rental.setSurface(createRentalDTO.getSurface());
        rental.setPrice(createRentalDTO.getPrice());
        rental.setDescription(createRentalDTO.getDescription());
        rental.setOwner(new User(ownerId));
        rental.setCreatedAt(new Date());
        rental.setUpdatedAt(new Date());

        if (picture != null && !picture.isEmpty()) {
            rental.setPicture(imageStorageService.saveImage(picture).orElse(null));
        }

        return convertToDTO(rentalRepository.save(rental));
    }

    /**
     * Met à jour une location existante.
     * @param id ID de la location
     * @param rentalUpdates DTO contenant les informations mises à jour
     * @param ownerId ID du propriétaire
     * @return RentalDTO de la location mise à jour
     */
    public RentalDTO updateRental(Long id, UpdateRentalDTO rentalUpdates, Long ownerId) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Location non trouvée avec ID : " + id));

        rental.setName(rentalUpdates.getName());
        rental.setSurface(rentalUpdates.getSurface());
        rental.setPrice(rentalUpdates.getPrice());
        rental.setDescription(rentalUpdates.getDescription());
        rental.setUpdatedAt(new Date());

        return convertToDTO(rentalRepository.save(rental));
    }

    /**
     * Convertit une entité Rental en DTO.
     * @param rental Entité Rental
     * @return RentalDTO correspondant
     */
    private RentalDTO convertToDTO(Rental rental) {
        return new RentalDTO(
                rental.getId(),
                rental.getName(),
                rental.getDescription(),
                (int) rental.getPrice(),
                rental.getSurface(),
                rental.getPicture(),
                convertToLocalDateTime(rental.getCreatedAt()),
                convertToLocalDateTime(rental.getUpdatedAt()),
                rental.getOwner().getId());
    }

    /**
     * Convertit une Date en LocalDateTime.
     * @param date Date à convertir
     * @return LocalDateTime correspondant
     */
    private LocalDateTime convertToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}