package com.wedding.venue.model;

import java.time.LocalDate; // Import LocalDate

public class ReserveRequest {
    private Long venueId; // Changed type to Long
    private LocalDate date; // Changed type to LocalDate
    private String location;

    public ReserveRequest() {}

    public ReserveRequest(Long venueId, LocalDate date, String location) {
        this.venueId = venueId;
        this.date = date;
        this.location = location;
    }

    // Getters and setters
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public LocalDate getDate() { return date; } // Getter returns LocalDate
    public void setDate(LocalDate date) { this.date = date; } // Setter accepts LocalDate
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}