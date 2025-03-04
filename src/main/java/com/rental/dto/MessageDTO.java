package com.rental.dto;

import java.util.Base64;

public class MessageDTO {

    private String message; // Correspond à "message" dans le JSON
    private Long userId;    // Correspond à "user_id" dans le JSON
    private Long rentalId;  // Correspond à "rental_id" dans le JSON

    // Getters et Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    // Add base64Url encoding for the message body
    public String getEncodedMessage() {
        return Base64.getUrlEncoder().encodeToString(message.getBytes());
    }

    // Add base64Url decoding for the message body
    public void setEncodedMessage(String encodedMessage) {
        this.message = new String(Base64.getUrlDecoder().decode(encodedMessage));
    }
}
