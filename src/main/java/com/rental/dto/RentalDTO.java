package com.rental.dto;

public class RentalDTO {
    private int id;
    private String name;

    // Constructeurs
    public RentalDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}