package com.wedding.broker.model;

public class VenueReserveRequest {
    private String venueId;
    private String date;
    private String location;

    public VenueReserveRequest() {}

    public VenueReserveRequest(String venueId, String date, String location) {
        this.venueId = venueId;
        this.date = date;
        this.location = location;
    }

    // Getters and setters
    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}