package com.wedding.broker.model;

public class Venue {
    private String id;
    private String name;
    private String images;
    private int price;
    private String style;
    private int capacity;
    private double rating;
    private boolean available;

    // Constructor
    public Venue(String id, String name, String image, int price, String style, int capacity, double rating, boolean available) {
        this.id = id;
        this.name = name;
        this.images = image;
        this.price = price;
        this.style = style;
        this.capacity = capacity;
        this.rating = rating;
        this.available = available;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getImages() { return images; }
    public int getPrice() { return price; }
    public String getStyle() { return style; }
    public int getCapacity() { return capacity; }
    public double getRating() { return rating; }
    public boolean isAvailable() { return available; }
}