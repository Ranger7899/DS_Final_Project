package com.wedding.photographer.model;
import java.time.LocalDate;

public class ReserveRequest {
    private Long photoId;
    private LocalDate date;
    private String location;


    public ReserveRequest(){}

    public ReserveRequest(Long photoId, LocalDate date, String location){
        this.photoId = photoId;
        this.date = date;
        this.location = location;
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
}
