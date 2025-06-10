package com.wedding.broker.model;

public class Catering {
    private String id;
    private String name;
    private String images;
    private int price;
    private double rating;
    private boolean available;
    private int maxEventsPerDay;
    private String foodType;

    public Catering(String id, String name, String images, int price, double rating, boolean available, int maxEventsPerDay, String foodType) {
        this.id = id;
        this.name = name;
        this.images = images;
        this.price = price;
        this.rating = rating;
        this.available = available;
        this.maxEventsPerDay = maxEventsPerDay;
        this.foodType = foodType;
    }

    // Getters (no setters for now, consistent with Photographer)
    public String getId() { return id; }
    public String getName() { return name; }
    public String getImages() { return images; }
    public int getPrice() { return price; }
    public double getRating() { return rating; }
    public boolean isAvailable() { return available; }
    public int getMaxEventsPerDay() { return maxEventsPerDay; }

    public String getFoodType() { return foodType; }
}
