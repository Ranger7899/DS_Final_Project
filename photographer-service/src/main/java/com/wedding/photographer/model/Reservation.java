package com.wedding.photographer.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "photo_id")
    private Long photoId;

    @Column(columnDefinition = "DATE")
    private LocalDate date;

    private String location;
    private String status; // pending confirmed or cancelled

    public Reservation(){}
    public Reservation( Long photoId, LocalDate date, String location, String status){
        this.photoId = photoId;
        this.date = date;
        this.location = location;
        this.status = status;
    }
    public Reservation(Long id, Long photoId, LocalDate date, String location, String status){
        this.id = id;
        this.photoId = photoId;
        this.date = date;
        this.location = location;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", photoId=" + photoId +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
