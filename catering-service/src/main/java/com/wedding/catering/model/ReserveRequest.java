package com.wedding.catering.model;

import java.time.LocalDate;
import java.util.List;

// Simple POJO for reservation requests
public class ReserveRequest {
    private Long companyId;
    private LocalDate date;
    private String location;
    private List<FoodType> foodTypes;

    public ReserveRequest() {}

    public ReserveRequest(Long companyId, LocalDate date, String location, List<FoodType> foodTypes) {
        this.companyId = companyId;
        this.date = date;
        this.location = location;
        this.foodTypes = foodTypes;
    }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<FoodType> getFoodTypes() { return foodTypes; }
    public void setFoodTypes(List<FoodType> foodTypes) { this.foodTypes = foodTypes; }
}
