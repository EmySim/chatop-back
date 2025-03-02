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
    private final FileStorageService fileStorageService;

    @Autowired
    public RentalService(RentalRepository rentalRepository, FileStorageService fileStorageService) {
        this.rentalRepository = rentalRepository;
        this.fileStorageService = fileStorageService;
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
        rentalDTO.setPicture(rental.getPicture());
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
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + id));
        return convertToDTO(rental);
    }

    /**
     * Creates a new rental.
     *
     * @param createRentalDTO The DTO containing rental details.
     * @return The created RentalDTO.
     */
    public RentalDTO createRental(CreateRentalDTO createRentalDTO) {
        Rental rental = new Rental();
        rental.setName(createRentalDTO.getName());
        rental.setDescription(createRentalDTO.getDescription());
        rental.setPrice(createRentalDTO.getPrice());
        rental.setLocation(createRentalDTO.getLocation());
        rental.setSurface(createRentalDTO.getSurface());
        rental.setPicture(createRentalDTO.getPicture());
        rental.setOwner_id(createRentalDTO.getOwner_id());
        rental.setCreatedAt(new Date());
        rental.setUpdatedAt(new Date());

        Rental savedRental = rentalRepository.save(rental);
        return convertToDTO(savedRental);
    }

    /**
     * Updates an existing rental.
     *
     * @param id The ID of the rental to update.
     * @param updateRentalDTO The DTO containing updated rental details.
     * @return The updated RentalDTO.
     */
    public RentalDTO updateRental(Long id, UpdateRentalDTO updateRentalDTO) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found with id: " + id));

        rental.setName(updateRentalDTO.getName());
        rental.setDescription(updateRentalDTO.getDescription());
        rental.setPrice(updateRentalDTO.getPrice());
        rental.setLocation(updateRentalDTO.getLocation());
        rental.setSurface(updateRentalDTO.getSurface());
        rental.setPicture(updateRentalDTO.getPicture());
        rental.setOwner_id(updateRentalDTO.getOwner_id());
        rental.setUpdatedAt(new Date());

        Rental updatedRental = rentalRepository.save(rental);
        return convertToDTO(updatedRental);
    }

    /**
     * Creates a new rental with file upload.
     *
     * @param name The name of the rental.
     * @param description The description of the rental.
     * @param price The price of the rental.
     * @param location The location of the rental.
     * @param file The image file.
     * @return The created RentalDTO.
     */
    public RentalDTO createRentalWithFile(String name, String description, Double price, String location, MultipartFile file) {
        try {
            String pictureUrl = fileStorageService.storeFile(file);

            Rental rental = new Rental();
            rental.setName(name);
            rental.setDescription(description);
            rental.setPrice(price);
            rental.setLocation(location);
            rental.setSurface(0); // Set default surface value
            rental.setPicture(pictureUrl);
            rental.setOwner_id(0L); // Set default owner_id value
            rental.setCreatedAt(new Date());
            rental.setUpdatedAt(new Date());

            Rental savedRental = rentalRepository.save(rental);
            return convertToDTO(savedRental);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating rental with file", e);
            throw new RuntimeException("Error creating rental with file", e);
        }
    }
}
