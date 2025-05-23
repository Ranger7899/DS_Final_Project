package com.wedding.venue.model;

import java.time.LocalDate; // Import for LocalDate

public class Venue {
    private String id;
    private String name;
    private String location;
    private LocalDate date; // Use LocalDate for dates
    private boolean available;

    public Venue() {}

    public Venue(String id, String name, String location, LocalDate date, boolean available) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.available = available;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}