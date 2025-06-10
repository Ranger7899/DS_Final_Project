package com.wedding.catering.model;

import jakarta.persistence.*;

@Entity
@Table(name = "caterings")
public class CateringCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private boolean available;

    private int maxEventsPerDay;

    private Double price;
    private Integer rating;
    private String images;

    // 👇 NEW: Single food type as plain String
    private String foodType;

    public CateringCompany() {}

    public CateringCompany(String name, String location, int maxEventsPerDay, Double price, Integer rating, String images, boolean available, String foodType) {
        this.name = name;
        this.location = location;
        this.maxEventsPerDay = maxEventsPerDay;
        this.price = price;
        this.rating = rating;
        this.images = images;
        this.available = available;
        this.foodType = foodType;
    }

    public CateringCompany(Long id, String name, String location, int maxEventsPerDay, Double price, Integer rating, String images, boolean available, String foodType) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.maxEventsPerDay = maxEventsPerDay;
        this.price = price;
        this.rating = rating;
        this.images = images;
        this.available = available;
        this.foodType = foodType;
    }

    // Getters
    public Long getId() { return id; }

    public String getName() { return name; }

    public String getLocation() { return location; }

    public boolean isAvailable() { return available; }

    public int getMaxEventsPerDay() { return maxEventsPerDay; }

    public Double getPrice() { return price; }

    public Integer getRating() { return rating; }

    public String getImages() { return images; }

    public String getFoodType() { return foodType; }

    // Setters
    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setLocation(String location) { this.location = location; }

    public void setAvailable(boolean available) { this.available = available; }

    public void setMaxEventsPerDay(int maxEventsPerDay) { this.maxEventsPerDay = maxEventsPerDay; }

    public void setPrice(Double price) { this.price = price; }

    public void setRating(Integer rating) { this.rating = rating; }

    public void setImages(String images) { this.images = images; }

    public void setFoodType(String foodType) { this.foodType = foodType; }

    @Override
    public String toString() {
        return "CateringCompany{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", maxEventsPerDay=" + maxEventsPerDay +
                ", price=" + price +
                ", available=" + available +
                ", rating=" + rating +
                ", images='" + images + '\'' +
                ", foodType='" + foodType + '\'' +
                '}';
    }
}
