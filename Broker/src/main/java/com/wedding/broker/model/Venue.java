// src/main/java/com/wedding/broker/model/Venue.java
package com.wedding.broker.model;

public class Venue {
    private String id;
    private String name;
    private String location;
    private boolean available; // Matches the venue service's Venue model

    // Default constructor
    public Venue() {
    }

    // Constructor with all fields
    public Venue(String id, String name, String location, boolean available) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.available = available;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}