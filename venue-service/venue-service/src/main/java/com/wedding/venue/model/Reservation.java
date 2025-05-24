package com.wedding.venue.model;

import jakarta.persistence.*; // Import all necessary JPA annotations
import java.time.LocalDate; // Import LocalDate for date handling

@Entity // Marks this class as a JPA entity
@Table(name = "reservations") // Maps this entity to a database table named 'reservations'
public class Reservation {
    @Id // Marks 'id' as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures ID to be auto-incremented by the database
    private Long id; // Changed ID type to Long

    // Use Long to match the new Venue ID type
    @Column(name = "venue_id") // Maps to a column named 'venue_id' in the database
    private Long venueId;

    @Column(columnDefinition = "DATE") // Ensures LocalDate is mapped to a DATE type in MySQL
    private LocalDate date; // Changed date type to LocalDate

    private String location;
    private String status; // e.g., "pending", "confirmed", "cancelled"

    // Default constructor is required by JPA
    public Reservation() {}

    // Constructor for creating new Reservations without an ID
    public Reservation(Long venueId, LocalDate date, String location, String status) {
        this.venueId = venueId;
        this.date = date;
        this.location = location;
        this.status = status;
    }

    // Full constructor (useful for fetching from DB)
    public Reservation(Long id, Long venueId, LocalDate date, String location, String status) {
        this.id = id;
        this.venueId = venueId;
        this.date = date;
        this.location = location;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", venueId=" + venueId +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}