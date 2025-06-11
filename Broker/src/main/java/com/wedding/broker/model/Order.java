package com.wedding.broker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
// import java.sql.Date; // If 'date' column is java.sql.Date - UNUSED NOW

@Entity
@Table(name = "orders", schema = "dbo") // Specify schema if needed, adjust if default schema
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assuming 'id' is auto-incrementing
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "date")
    private String date; // Based on your image, 'date' is varchar, so String

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_id")
    private String userId; // Based on your image, 'user_id' is varchar

    // New columns for services
    @Column(name = "venue_id")
    private String venueId; // Adjust type based on your actual venue_id type
    @Column(name = "venue_name")
    private String venueName;
    @Column(name = "catering_id")
    private String cateringId;
    @Column(name = "catering_name")
    private String cateringName;
    @Column(name = "photographer_id")
    private String photographerId;
    @Column(name = "photographer_name")
    private String photographerName;

    @Column(name = "address")
    private String address;

    @Column(name = "payment_details") // Consider carefully how you handle sensitive payment info
    private String paymentDetails;

    // Constructors
    public Order() {
        this.createdAt = LocalDateTime.now(); // Set default on creation
        this.updatedAt = LocalDateTime.now(); // Set default on creation
    }

    // You might want a constructor for specific fields if you don't use a DTO
    public Order(String date, String location, String status, String userId,
                 String venueId, String venueName, String cateringId, String cateringName,
                 String photographerId, String photographerName, String address, String paymentDetails) {
        this(); // Calls the default constructor to set timestamps
        this.date = date;
        this.location = location;
        this.status = status;
        this.userId = userId;
        this.venueId = venueId;
        this.venueName = venueName;
        this.cateringId = cateringId;
        this.cateringName = cateringName;
        this.photographerId = photographerId;
        this.photographerName = photographerName;
        this.address = address; // NEW
        this.paymentDetails = paymentDetails; // NEW
    }

    // Getters and Setters (Important for Spring to bind form data)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public String getCateringId() { return cateringId; }
    public void setCateringId(String cateringId) { this.cateringId = cateringId; }
    public String getCateringName() { return cateringName; }
    public void setCateringName(String cateringName) { this.cateringName = cateringName; }
    public String getPhotographerId() { return photographerId; }
    public void setPhotographerId(String photographerId) { this.photographerId = photographerId; }
    public String getPhotographerName() { return photographerName; }
    public void setPhotographerName(String photographerName) { this.photographerName = photographerName; }

    // NEW GETTERS AND SETTERS
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(String paymentDetails) { this.paymentDetails = paymentDetails; }

    // Optional: toString() for logging/debugging
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", updatedAt=" + updatedAt +
                ", userId='" + userId + '\'' +
                ", venueId='" + venueId + '\'' +
                ", venueName='" + venueName + '\'' +
                ", cateringId='" + cateringId + '\'' +
                ", cateringName='" + cateringName + '\'' +
                ", photographerId='" + photographerId + '\'' +
                ", photographerName='" + photographerName + '\'' +
                ", address='" + address + '\'' +
                ", paymentDetails='" + paymentDetails + '\'' +
                '}';
    }
}