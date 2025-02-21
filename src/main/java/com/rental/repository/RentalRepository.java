package com.rental.repository;

import com.rental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Rental entity.
 * No additional methods needed for basic CRUD operations.
 */
public interface RentalRepository extends JpaRepository<Rental, Long> {
    // JpaRepository already provides the necessary methods (findById, save, deleteById, etc.)
}
