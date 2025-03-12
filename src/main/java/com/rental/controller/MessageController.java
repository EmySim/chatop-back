package com.rental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rental.dto.MessageDTO;
import com.rental.dto.SnackbarNotif;
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

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Endpoint for sending a new message.
     * 
     * @param messageDTO the message data transfer object containing the message details
     * @return a ResponseEntity containing a SnackbarNotif with the result of the operation
     */
    @Operation(summary = "Send a new message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid message data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<SnackbarNotif> sendMessage(@Valid @RequestBody MessageDTO messageDTO) {
        // Validate the message data
        if (messageDTO.getRentalId() == null) {
            return ResponseEntity.badRequest().body(new SnackbarNotif(null, "Rental ID must not be null"));
        }
        if (messageDTO.getUserId() == null) {
            return ResponseEntity.badRequest().body(new SnackbarNotif(null, "User ID must not be null"));
        }

        // Send the message via the service
        try {
            messageService.sendMessage(messageDTO);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new SnackbarNotif(null, "An error occurred while sending the message"));
        }

        // Return success response with the message details
        return ResponseEntity.ok(new SnackbarNotif(messageDTO, "Message sent successfully"));
    }
}