package com.wedding.catering.model;

import jakarta.persistence.*;

@Entity
@Table(name = "catering_companies")
public class CateringCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    private int maxEventsPerDay; // Max number of events this catering can handle in a day

    public CateringCompany() {}

    public CateringCompany(String name, String location, int maxEventsPerDay) {
        this.name = name;
        this.location = location;
        this.maxEventsPerDay = maxEventsPerDay;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getMaxEventsPerDay() {
        return maxEventsPerDay;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMaxEventsPerDay(int maxEventsPerDay) {
        this.maxEventsPerDay = maxEventsPerDay;
    }
}
