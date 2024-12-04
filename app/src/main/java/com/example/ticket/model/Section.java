package com.example.ticket.model;

public class Section {
    private int sectionId;
    private int locationId;
    private String name;
    private int seatCapacity;

    // Constructor
    public Section(int sectionId, int locationId, String name, int seatCapacity) {
        this.sectionId = sectionId;
        this.locationId = locationId;
        this.name = name;
        this.seatCapacity = seatCapacity;
    }


    // Getters and Setters
    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(int seatCapacity) {
        this.seatCapacity = seatCapacity;
    }
}
