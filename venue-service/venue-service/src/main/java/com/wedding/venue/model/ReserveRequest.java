package com.wedding.venue.model;

public class ReserveRequest {
    private String venueId;
    private String date;
    private String location;
    private int timeout; // Not strictly needed on the receiving end for logic, but good for matching

    public ReserveRequest() {}

    public ReserveRequest(String venueId, String date, String location, int timeout) {
        this.venueId = venueId;
        this.date = date;
        this.location = location;
        this.timeout = timeout;
    }

    // Getters and setters
    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
}