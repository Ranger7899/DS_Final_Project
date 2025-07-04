package com.wedding.catering.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "catering_company_id")
    private Long cateringCompanyId;

    @Column(columnDefinition = "DATE")
    private LocalDate date;

    private String location;

    private String status; // "pending", "confirmed", "cancelled"
    @Column(name = "created", nullable = false, updatable = false )
    private LocalDateTime created;

    @PrePersist
    protected void onCreateRecord(){
        this.created = LocalDateTime.now();
    }

    public Reservation() {}

    public Reservation(Long cateringCompanyId, LocalDate date, String location, String status) {
        this.cateringCompanyId = cateringCompanyId;
        this.date = date;
        this.location = location;
        this.status = status;

    }

    public Long getId() {
        return id;
    }

    public Long getCateringCompanyId() {
        return cateringCompanyId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCateringCompanyId(Long cateringCompanyId) {
        this.cateringCompanyId = cateringCompanyId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getCreated() {return created; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", cateringCompanyId=" + cateringCompanyId +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}
