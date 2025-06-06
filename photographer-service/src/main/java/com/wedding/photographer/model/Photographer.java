package com.wedding.photographer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "photographers")
public class Photographer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private boolean available; // Foe general avaliablity not by date

    public Photographer(){}

    public Photographer(String name, String location, boolean avaliable){
        this.name = name;
        this.location = location;
        this.available = avaliable;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}
    public boolean isAvailable() {return available;}
    public void setAvailable(boolean available) {this.available = available;}

    @Override
    public String toString(){
        return "Photographer{ "+
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", available=" + available +
                '}';
    }
}
