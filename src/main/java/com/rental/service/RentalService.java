package com.rental.service;

import com.rental.dto.RentalDTO;
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
}