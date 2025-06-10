package com.wedding.broker.model;

public class PhotographerReserveRequest {
    private String photoId;
    private String date;
    private String location;

    public PhotographerReserveRequest() {}

    public PhotographerReserveRequest(String photoId, String date, String location) {
        this.photoId = photoId;
        this.date = date;
        this.location = location;
    }

    // Getters and setters
    public String getPhotoId() { return photoId; }
    public void setPhotoId(String photoId) { this.photoId = photoId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}