package com.wedding.broker.model;

public class OrderRequest {
    private String userId;
    private String date;
    private String location;
    private String venueId;
    private String cateringId;
    private String photographerId;
    private String venueReservationId;
    private String photographerReservationId;
    private String cateringReservationId;
    private String address;
    private String paymentDetails;

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }

    public String getCateringId() { return cateringId; }
    public void setCateringId(String cateringId) { this.cateringId = cateringId; }

    public String getPhotographerId() { return photographerId; }
    public void setPhotographerId(String photographerId) { this.photographerId = photographerId; }

    public String getVenueReservationId() { return venueReservationId; }
    public void setVenueReservationId(String venueReservationId) { this.venueReservationId = venueReservationId; }

    public String getPhotographerReservationId() { return photographerReservationId; }
    public void setPhotographerReservationId(String photographerReservationId) { this.photographerReservationId = photographerReservationId; }

    public String getCateringReservationId() { return cateringReservationId; }
    public void setCateringReservationId(String cateringReservationId) { this.cateringReservationId = cateringReservationId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(String paymentDetails) { this.paymentDetails = paymentDetails; }
}
