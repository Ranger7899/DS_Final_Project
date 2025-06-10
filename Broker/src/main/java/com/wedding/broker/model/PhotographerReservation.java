package com.wedding.broker.model;

public class PhotographerReservation {
    private String id;
    private String photoId; // Corresponds to photoId in venue service
    private String date; // Corresponds to date in venue service (as String)
    private String location; // Corresponds to location in venue service
    private String status; // Corresponds to status in venue service (e.g., "pending", "confirmed")

    public PhotographerReservation() {
    }

    public PhotographerReservation(String id, String photoId, String date, String location, String status) {
        this.id = id;
        this.photoId = photoId;
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

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
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