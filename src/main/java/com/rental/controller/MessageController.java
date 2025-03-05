package com.rental.controller;

import com.rental.dto.MessageDTO;
import com.rental.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.logging.Logger;

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

        // Decode the message body with base64Url before processing
        String decodedMessage = new String(Base64.getUrlDecoder().decode(messageDTO.getMessage()));
        messageDTO.setMessage(decodedMessage);

        messageService.sendMessage(messageDTO);

        // Encode the message body with base64Url before sending
        String encodedMessage = Base64.getUrlEncoder().encodeToString("Message sent successfully".getBytes());
        logger.info("Message sent successfully.");
        return ResponseEntity.ok(encodedMessage);
    }
}
