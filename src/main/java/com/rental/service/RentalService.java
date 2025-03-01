package com.rental.service;

import com.rental.dto.CreateRentalDTO;
import com.rental.dto.RentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.Date;

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
        return rentalRepository.findAll();
    }

    /**
     * Converts a list of Rental entities to a list of RentalDTOs.
     *
     * @return List of RentalDTOs.
     */
    public List<RentalDTO> getAllRentalDTOs() {
        List<Rental> rentals = rentalRepository.findAll();
        return rentals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Rental entity to a RentalDTO.
     *
     * @param rental The Rental entity to convert.
     * @return The converted RentalDTO.
     * @throws IllegalArgumentException if the rental object is null.
     */
    private RentalDTO convertToDTO(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null");
        }

        RentalDTO rentalDTO = new RentalDTO();
        rentalDTO.setId(rental.getId().intValue());
        rentalDTO.setName(rental.getName());
        rentalDTO.setDescription(rental.getDescription());
        rentalDTO.setPrice(rental.getPrice());
        rentalDTO.setLocation(rental.getLocation());
        rentalDTO.setCreatedAt(rental.getCreatedAt());
        rentalDTO.setUpdatedAt(rental.getUpdatedAt());
        rentalDTO.setSurface(rental.getSurface());
        rental.setPictureUrl(pictureUrl);
        rentalDTO.setOwner_id(rental.getOwner_id());

        return rentalDTO;
    }

    /**
     * Retrieves a rental by ID.
     *
     * @param id The ID of the rental.
     * @return The RentalDTO of the rental.
     */
    public RentalDTO getRentalById(Long id) {
        // Implementation omitted for shortness
        return null; // Placeholder
    }

    /**
     * Creates a new rental.
     *
     * @param createRentalDTO The DTO containing rental details.
     * @return The created RentalDTO.
     */
    public RentalDTO createRental(CreateRentalDTO createRentalDTO) {
        // Implementation omitted for shortness
        return null; // Placeholder
    }

    /**
     * Updates an existing rental.
     *
     * @param id The ID of the rental to update.
     * @param updateRentalDTO The DTO containing updated rental details.
     * @return The updated RentalDTO.
     */
    public RentalDTO updateRental(Long id, UpdateRentalDTO updateRentalDTO) {
        // Implementation omitted for shortness
        return null; // Placeholder
    }

    public RentalDTO createRentalWithFile(String name, String description, Double price, String location, MultipartFile file) {
    }
}