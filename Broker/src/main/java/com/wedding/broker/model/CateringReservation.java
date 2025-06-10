package com.wedding.broker.model;

public class CateringReservation {
    private String id;
    private String companyId;
    private String date;
    private String location;
    private String status;

    public CateringReservation() {}

    public CateringReservation(String id, String companyId, String date, String location, String status) {
        this.id = id;
        this.companyId = companyId;
        this.date = date;
        this.location = location;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
