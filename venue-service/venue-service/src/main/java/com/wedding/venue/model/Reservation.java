package com.wedding.venue.model;

public class Reservation {
    private String id;
    private String venueId; // Changed from supplierId to venueId for clarity
    private String date;
    private String location;
    private String status; // e.g., "pending", "confirmed", "cancelled"

    public Reservation() {}

    public Reservation(String id, String venueId, String date, String location, String status) {
        this.id = id;
        this.venueId = venueId;
        this.date = date;
        this.location = location;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}