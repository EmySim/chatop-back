package com.rental.service;

import com.rental.dto.MessageDTO;
import com.rental.entity.Message;
import com.rental.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Service for handling message-related operations.
 */
@Service
public class MessageService {

    private static final Logger logger = Logger.getLogger(MessageService.class.getName());
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Processes the logic for sending a new message.
     *
     * @param messageDTO The DTO containing message details.
     */
    public void sendMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setMessage(messageDTO.getMessage()); // Mappe "message" du JSON
        message.setUserId(messageDTO.getUserId());   // Mappe "user_id" du JSON
        message.setRentalId(messageDTO.getRentalId()); // Mappe "rental_id" du JSON
        message.setCreatedAt(LocalDateTime.now());  // Initialise à l'heure actuelle
        message.setUpdatedAt(LocalDateTime.now());  // Initialise à l'heure actuelle

        messageRepository.save(message); // Sauvegarde dans la base de données
        logger.info("Message registered successfully for user ID: " + message.getUserId() +
                ", rental ID: " + message.getRentalId());
    }
}
