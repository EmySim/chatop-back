package com.rental.dto;

public class SnackbarNotif {

    private Object data;  // Peut contenir un RentalDTO, MessageDTO ou tout autre DTO
    private String message;

    // Constructeur
    public SnackbarNotif(Object data, String message) {
        this.data = data;
        this.message = message;
    }

    // Getters et setters
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}