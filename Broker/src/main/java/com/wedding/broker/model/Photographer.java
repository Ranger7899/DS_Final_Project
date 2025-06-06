package com.wedding.broker.model;

public class Photographer {
    private String id;
    private String name;
    private String image;
    private int price;
    private String style;
    private double rating;
    private boolean available;

    // Constructor
    public Photographer(String id, String name, String image, int price, String style, double rating, boolean available) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.style = style;
        this.rating = rating;
        this.available = available;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public int getPrice() { return price; }
    public String getStyle() { return style; }
    public double getRating() { return rating; }
    public boolean isAvailable() { return available; }
}