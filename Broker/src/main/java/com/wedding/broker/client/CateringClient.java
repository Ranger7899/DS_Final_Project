package com.wedding.broker.client;

import com.wedding.broker.model.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class CateringClient {

    private final RestTemplate restTemplate;
    private final String cateringApiUrl;

    public CateringClient(RestTemplate restTemplate, @Value("http://localhost:8084") String cateringApiUrl) {
        this.restTemplate = restTemplate;
        this.cateringApiUrl = cateringApiUrl;
    }

    public List<Catering> getAvailableCompanies(String date, String location) {
        return restTemplate.exchange(
                cateringApiUrl + "/catering/available?date={date}&location={location}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Catering>>() {},
                date, location
        ).getBody();
    }

    public Catering getCateringCompanyById(String companyId) {
        return restTemplate.getForObject(cateringApiUrl + "/catering/{id}", Catering.class, companyId);
    }

    public CateringReservation getCateringReservationById(String reservationId){
        return  restTemplate.getForObject(cateringApiUrl+ "/catering/reservation/{id}", CateringReservation.class, reservationId);
    }

    public CateringReservation reserve(String companyId, String date, String location) {
        CateringReserveRequest request = new CateringReserveRequest(companyId, date, location);
        return restTemplate.postForObject(cateringApiUrl + "/catering/reserve", request, CateringReservation.class);
    }

    public void confirm(String reservationId) {
        restTemplate.postForObject(cateringApiUrl + "/catering/confirm/{id}", null, Void.class, reservationId);
    }

    public void cancel(String reservationId) {
        restTemplate.postForObject(cateringApiUrl + "/catering/cancel/{id}", null, Void.class, reservationId);
    }
}
