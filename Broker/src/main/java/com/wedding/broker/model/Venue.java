package com.wedding.broker.model;

public class Venue {
    private String id;
    private String name;
    private String location;
    private String date;
    private boolean available;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}