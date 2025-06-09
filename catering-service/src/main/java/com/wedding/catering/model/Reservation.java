package com.wedding.catering.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

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


    private String status; // e.g., pending, confirmed, cancelled

    @ElementCollection(targetClass = FoodType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "reservation_foodtypes", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "food_type")
    private List<FoodType> foodTypes;

    public Reservation() {}

    public Reservation(Long cateringCompanyId, LocalDate date, String location, String status, List<FoodType> foodTypes) {
        this.cateringCompanyId = cateringCompanyId;
        this.date = date;
        this.location = location;
        this.status = status;
        this.foodTypes = foodTypes;
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

    public List<FoodType> getFoodTypes() {
        return foodTypes;
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

    public void setFoodTypes(List<FoodType> foodTypes) {
        this.foodTypes = foodTypes;
    }
}
