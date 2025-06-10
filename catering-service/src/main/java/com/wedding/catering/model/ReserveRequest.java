package com.wedding.catering.model;

import java.time.LocalDate;

public class ReserveRequest {

    private Long companyId;
    private LocalDate date;
    private String location;

    public ReserveRequest() {}

    public ReserveRequest(Long companyId, LocalDate date, String location, int timeout) {
        this.companyId = companyId;
        this.date = date;
        this.location = location;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
