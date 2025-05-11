package com.wedding.broker.client;

import com.wedding.broker.model.Reservation;
import com.wedding.broker.model.ReserveRequest;
import com.wedding.broker.model.Venue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class VenueClient {
    private final RestTemplate restTemplate;
    private final String venueApiUrl;

    public VenueClient(RestTemplate restTemplate, @Value("${venue.api.url}") String venueApiUrl) {
        this.restTemplate = restTemplate;
        this.venueApiUrl = venueApiUrl;
    }

    public List<Venue> getAvailableVenues(String date, String location) {
        return restTemplate.getForObject(venueApiUrl + "/available?date={date}&location={location}", List.class, date, location);
    }

    public Reservation reserve(String venueId, String date, String location, int timeout) {
        ReserveRequest request = new ReserveRequest(venueId, date, location, timeout);
        return restTemplate.postForObject(venueApiUrl + "/reserve", request, Reservation.class);
    }

    public void confirm(String reservationId) {
        restTemplate.postForObject(venueApiUrl + "/confirm/{id}", null, Void.class, reservationId);
    }

    public void cancel(String reservationId) {
        restTemplate.postForObject(venueApiUrl + "/cancel/{id}", null, Void.class, reservationId);
    }
}