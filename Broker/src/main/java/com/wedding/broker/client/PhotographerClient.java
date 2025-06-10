package com.wedding.broker.client;

import com.wedding.broker.model.Photographer;
import com.wedding.broker.model.PhotographerReservation;
import com.wedding.broker.model.PhotographerReserveRequest;
import com.wedding.broker.model.VenueReservation;
import com.wedding.broker.model.VenueReserveRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class PhotographerClient {
    private final RestTemplate restTemplate;
    private final String photographerApiUrl;

    public PhotographerClient(RestTemplate restTemplate, @Value("${photographer.api.url}") String photographerApiUrl) {
        this.restTemplate = restTemplate;
        this.photographerApiUrl = photographerApiUrl;
    }

    public List<Photographer> getAvailablePhotographers(String date, String location) {
        // Use ParameterizedTypeReference to correctly deserialize List<photographer>
        return restTemplate.exchange(
                photographerApiUrl + "/photographers/available?date={date}&location={location}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Photographer>>() {}, // Correctly handle generic list type
                date, location
        ).getBody();
    }

    public Photographer getPhotographerById(String photographerId) {
        return restTemplate.getForObject(photographerApiUrl + "/photographers/{id}", Photographer.class, photographerId);
    }
    
    public PhotographerReservation reserve(String photographerId, String date, String location) {
        PhotographerReserveRequest request = new PhotographerReserveRequest(photographerId, date, location);
        // The photographer service's /reserve endpoint returns a Reservation object
        return restTemplate.postForObject(photographerApiUrl + "/photographers/reserve", request, PhotographerReservation.class);
    }

    public void confirm(String reservationId) {
        // The photographer service's /confirm/{id} endpoint expects an ID and returns no content
        restTemplate.postForObject(photographerApiUrl + "/photographers/confirm/{id}", null, Void.class, reservationId);
    }

    public void cancel(String reservationId) {
        // The photographer service's /cancel/{id} endpoint expects an ID and returns no content
        restTemplate.postForObject(photographerApiUrl + "/photographers/cancel/{id}", null, Void.class, reservationId);
    }
}