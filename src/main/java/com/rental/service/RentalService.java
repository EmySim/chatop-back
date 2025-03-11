package com.rental.service;

import com.rental.dto.*;
import com.rental.entity.Rental;
import com.rental.entity.User;
import com.rental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException; // Import de l'exception standard
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des locations.
 */
@Service
@Transactional
public class RentalService {

    private static final Logger logger = Logger.getLogger(RentalService.class.getName());

    private final RentalRepository rentalRepository;
    private final ImageStorageService imageStorageService;

    public RentalService(RentalRepository rentalRepository, ImageStorageService imageStorageService) {
        this.rentalRepository = rentalRepository;
        this.imageStorageService = imageStorageService;
    }

    public List<RentalDTO> getAllRentals() {
        logger.info("Récupération de toutes les locations.");
        return rentalRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RentalDTO getRental(Long id) {
        return rentalRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new NoSuchElementException("Location non trouvée avec ID : " + id));
    }

    public RentalDTO createRental(CreateRentalDTO createRentalDTO, MultipartFile picture, Long ownerId) {
        logger.info("Création d'une nouvelle location.");

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

    private LocalDateTime convertToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}