package com.wedding.broker.model;

public class CateringReserveRequest {
    private String companyId;
    private String date;
    private String location;

    public CateringReserveRequest() {}

    public CateringReserveRequest(String companyId, String date, String location) {
        this.companyId = companyId;
        this.date = date;
        this.location = location;
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
}
