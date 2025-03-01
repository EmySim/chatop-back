package com.rental.service;

import com.rental.dto.RentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service for handling rental-related operations.
 */
@Service
public class RentalService {

    private static final Logger logger = Logger.getLogger(RentalService.class.getName());
    private final RentalRepository rentalRepository;

    @Autowired
    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    /**
     * Retrieves all rentals.
     *
     * @return List of rentals.
     */
    public List<Rental> getAllRentals() {
        logger.info("Début de la méthode getAllRentals");

        try {
            List<Rental> rentals = rentalRepository.findAll();
            logger.info("Fin de la méthode getAllRentals");
            return rentals;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des locations", e);
            throw new RuntimeException("Erreur lors de la récupération des locations", e);
        }
    }

    /**
     * Converts a list of Rental entities to a list of RentalDTOs.
     *
     * @return List of RentalDTOs.
     */
    public List<RentalDTO> getAllRentalDTOs() {
        List<Rental> rentals = getAllRentals();
        return rentals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Rental entity to a RentalDTO.
     *
     * @param rental The Rental entity to convert.
     * @return The converted RentalDTO.
     */
    private RentalDTO convertToDTO(Rental rental) {
        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setId(rental.getId());
        rentalDTO.setName(rental.getName());
        rentalDTO.setDescription(rental.getDescription());
        rentalDTO.setPrice(rental.getPrice());
        rentalDTO.setLocation(rental.getLocation());
        rentalDTO.setCreatedAt(rental.getCreatedAt());
        rentalDTO.setUpdatedAt(rental.getUpdatedAt());
        return rentalDTO;
    }

    /**
     * Retrieves a rental by ID.
     *
     * @param id The ID of the rental.
     * @return The RentalDTO of the rental.
     */
    public RentalDTO getRentalById(Long id) {
        logger.info("Début de la méthode getRentalById");

        try {
            Rental rental = rentalRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Location non trouvée"));
            logger.info("Fin de la méthode getRentalById");
            return convertToDTO(rental);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la location", e);
            throw new RuntimeException("Erreur lors de la récupération de la location", e);
        }
    }

    /**
     * Creates a new rental.
     *
     * @param createRentalDTO The DTO containing rental details.
     * @return The created RentalDTO.
     */
    public RentalDTO createRental(CreateRentalDTO createRentalDTO) {
        logger.info("Début de la méthode createRental");

        try {
            Rental rental = new Rental();
            rental.setName(createRentalDTO.getName());
            rental.setDescription(createRentalDTO.getDescription());
            rental.setPrice(createRentalDTO.getPrice());
            rental.setLocation(createRentalDTO.getLocation());
            rental = rentalRepository.save(rental);
            logger.info("Fin de la méthode createRental");
            return convertToDTO(rental);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la création de la location", e);
            throw new RuntimeException("Erreur lors de la création de la location", e);
        }
    }

    /**
     * Updates an existing rental.
     *
     * @param id The ID of the rental to update.
     * @param updateRentalDTO The DTO containing updated rental details.
     * @return The updated RentalDTO.
     */
    public RentalDTO updateRental(Long id, UpdateRentalDTO updateRentalDTO) {
        logger.info("Début de la méthode updateRental");

        try {
            Rental rental = rentalRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Location non trouvée"));
            rental.setName(updateRentalDTO.getName());
            rental.setDescription(updateRentalDTO.getDescription());
            rental.setPrice(updateRentalDTO.getPrice());
            rental.setLocation(updateRentalDTO.getLocation());
            rental = rentalRepository.save(rental);
            logger.info("Fin de la méthode updateRental");
            return convertToDTO(rental);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la location", e);
            throw new RuntimeException("Erreur lors de la mise à jour de la location", e);
        }
    }
}