// src/main/java/com/wedding/broker/model/Reservation.java
package com.wedding.broker.model;

public class VenueReservation {
    private String id;
    private String venueId; // Corresponds to venueId in venue service
    private String date; // Corresponds to date in venue service (as String)
    private String location; // Corresponds to location in venue service
    private String status; // Corresponds to status in venue service (e.g., "pending", "confirmed")

    public VenueReservation() {
    }

    public VenueReservation(String id, String venueId, String date, String location, String status) {
        this.id = id;
        this.venueId = venueId;
        this.date = date;
        this.location = location;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}