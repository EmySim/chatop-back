package com.rental.dto;

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
}