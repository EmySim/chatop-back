package com.rental.controller;

import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rental.dto.MessageDTO;
import com.rental.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * Controller for handling message-related operations.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger logger = Logger.getLogger(MessageController.class.getName());
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Endpoint for sending a new message.
     */
    @Operation(summary = "Send a new message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid message data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<String> sendMessage(@Valid @RequestBody MessageDTO messageDTO) {
        logger.info("Attempting to send message with content: " + messageDTO.getMessage());

        // Vérification des données dans le DTO
        if (messageDTO.getRentalId() == null) {
            logger.warning("Rental ID is null");
            return ResponseEntity.badRequest().body("Rental ID must not be null");
        }
        if (messageDTO.getUserId() == null) {
            logger.warning("User ID is null");
            return ResponseEntity.badRequest().body("User ID must not be null");
        }

        // Envoi du message via le service
        try {
            messageService.sendMessage(messageDTO);
        } catch (Exception e) {
            logger.severe("Failed to send message: " + e.getMessage());
            return ResponseEntity.status(500).body("An error occurred while sending the message");
        }

        // Si tout se passe bien
        logger.info("Message sent successfully.");
        return ResponseEntity.ok("Message sent successfully");
    }
}
