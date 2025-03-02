package com.rental.repository;

import com.rental.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Message entity.
 * Provides basic CRUD operations.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
}
