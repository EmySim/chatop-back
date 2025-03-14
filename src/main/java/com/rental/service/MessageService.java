package com.rental.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rental.dto.MessageDTO;
import com.rental.entity.Message;
import com.rental.entity.Rental;
import com.rental.entity.User;
import com.rental.repository.MessageRepository;
import com.rental.repository.RentalRepository;
import com.rental.repository.UserRepository;

/**
 * Service for handling message-related operations.
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, RentalRepository rentalRepository,
            UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Processes the logic for sending a new message.
     *
     * @param messageDTO The DTO containing message details.
     */
    public void sendMessage(MessageDTO messageDTO) {
        // Validate rental ID
        if (messageDTO.getRentalId() == null) {
            throw new IllegalArgumentException("Rental ID must not be null");
        }
        // Validate user ID
        if (messageDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        // Retrieve rental entity
        Rental rental = rentalRepository.findById(messageDTO.getRentalId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID"));
        // Retrieve user entity
        User user = userRepository.findById(messageDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        // Create and populate message entity
        Message message = new Message();
        message.setMessage(messageDTO.getMessage());
        message.setRental(rental);
        message.setUser(user);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        // Save message to the repository
        messageRepository.save(message);
    }
}