package com.rental.service;

import com.rental.dto.RentalDTO;
import com.rental.dto.CreateRentalDTO;
import com.rental.dto.UpdateRentalDTO;
import com.rental.entity.Rental;
import com.rental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
     * Récupère toutes les locations dans un flux réactif.
     * @return Flux de RentalDTO
     */
    public Flux<RentalDTO> getAllRentals() {
        return Flux.defer(() -> Flux.fromIterable(rentalRepository.findAll()))
                .map(this::mapToDTO)
                .doOnError(e -> logger.log(Level.SEVERE, "Erreur lors du chargement des locations", e));
    }

    /**
     * Récupère une location par ID.
     * @param id L'identifiant de la location
     * @return Un Mono<RentalDTO> ou une erreur si non trouvé
     */
    public Mono<RentalDTO> getRental(Long id) {
        return Mono.defer(() -> Mono.justOrEmpty(rentalRepository.findById(id)))
                .map(this::mapToDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("Location non trouvée")))
                .doOnError(e -> logger.log(Level.WARNING, "Erreur lors du chargement de la location ID: " + id, e));
    }

    /**
     * Crée une nouvelle location.
     * @param createRentalDTO Données de création
     * @param image Image optionnelle
     * @return Un Mono<RentalDTO>
     */
    public Mono<RentalDTO> createRental(CreateRentalDTO createRentalDTO, MultipartFile image) {
        return Mono.defer(() -> {
            Rental rental = new Rental();
            rental.setName(createRentalDTO.getName());
            rental.setDescription(createRentalDTO.getDescription());
            rental.setPrice(createRentalDTO.getPrice());
            rental.setLocation(createRentalDTO.getLocation());
            rental.setOwnerId(createRentalDTO.getOwnerId());
            rental.setSurface(createRentalDTO.getSurface());

            return Mono.justOrEmpty(image)
                    .flatMap(imageStorageService::storeImage)
                    .doOnNext(url -> rental.setPicture(url))
                    .then(Mono.fromCallable(() -> rentalRepository.save(rental)))
                    .map(this::mapToDTO);
        }).doOnError(e -> logger.log(Level.SEVERE, "Erreur lors de la création de la location", e));
    }

    /**
     * Met à jour une location existante.
     * @param id L'identifiant de la location.
     * @param updateRentalDTO Les données mises à jour.
     * @param image L'image mise à jour.
     * @return Un Mono<RentalDTO> avec les données mises à jour.
     */
    public Mono<RentalDTO> updateRental(Long id, UpdateRentalDTO updateRentalDTO, MultipartFile image) {
        return Mono.defer(() -> Mono.justOrEmpty(rentalRepository.findById(id)))
                .flatMap(existingRental -> {
                    existingRental.setName(Optional.ofNullable(updateRentalDTO.getName()).orElse(existingRental.getName()));
                    existingRental.setDescription(Optional.ofNullable(updateRentalDTO.getDescription()).orElse(existingRental.getDescription()));
                    existingRental.setPrice(Optional.ofNullable(updateRentalDTO.getPrice()).orElse(existingRental.getPrice()));
                    existingRental.setLocation(Optional.ofNullable(updateRentalDTO.getLocation()).orElse(existingRental.getLocation()));
                    existingRental.setSurface(Optional.ofNullable(updateRentalDTO.getSurface()).orElse(existingRental.getSurface()));

                    return Mono.justOrEmpty(image)
                            .flatMap(imageStorageService::storeImage)
                            .doOnNext(url -> existingRental.setPicture(url))
                            .thenReturn(existingRental);
                })
                .flatMap(rental -> Mono.fromCallable(() -> rentalRepository.save(rental)))
                .map(this::mapToDTO)
                .doOnError(e -> logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la location ID: " + id, e));
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
}