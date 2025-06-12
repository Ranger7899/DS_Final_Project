// src/main/java/com/wedding/broker/client/VenueClient.java
package com.wedding.broker.client;

import com.wedding.broker.model.PhotographerReservation;
import com.wedding.broker.model.VenueReservation;
import com.wedding.broker.model.VenueReserveRequest; // Keep this as is, it's consistent
import com.wedding.broker.model.Venue; // Ensure this imports the updated broker Venue model
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference; // Import this
import org.springframework.http.HttpMethod; // Import this
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
         // Use ParameterizedTypeReference to correctly deserialize List<Venue>
         return restTemplate.exchange(
                 venueApiUrl + "/venues/available?date={date}&location={location}",
                 HttpMethod.GET,
                 null,
                 new ParameterizedTypeReference<List<Venue>>() {}, // Correctly handle generic list type
                 date, location
         ).getBody();
     }

//    // Hardcoded. For testing purposes:
//    public List<Venue> getAvailableVenues(String date, String location) {
//        return List.of(
//            new Venue(
//                "v1",
//                "Sunset Garden",
//                "https://example.com/images/sunset-garden.jpg",
//                4500,
//                "Outdoor",
//                150,
//                4.7,
//                true
//            ),
//            new Venue(
//                "v2",
//                "Elegant Ballroom",
//                "https://example.com/images/elegant-ballroom.jpg",
//                6000,
//                "Indoor",
//                200,
//                4.9,
//                true
//            ),
//            new Venue(
//                "v3",
//                "Lakeside Pavilion",
//                "https://example.com/images/lakeside-pavilion.jpg",
//                5200,
//                "Waterfront",
//                120,
//                4.6,
//                true
//            )
//        );
//    }


    public Venue getVenueById(String venueId) {
        return restTemplate.getForObject(venueApiUrl + "/venues/{id}", Venue.class, venueId);
    }

    public VenueReservation getVenueReservationById(String reservationId){
        return  restTemplate.getForObject(venueApiUrl+ "/venues/reservation/{id}", VenueReservation.class, reservationId);
    }

    public VenueReservation reserve(String venueId, String date, String location) {
        VenueReserveRequest request = new VenueReserveRequest(venueId, date, location);
        // The venue service's /reserve endpoint returns a Reservation object
        return restTemplate.postForObject(venueApiUrl + "/venues/reserve", request, VenueReservation.class);
    }

    public void confirm(String reservationId) {
        // The venue service's /confirm/{id} endpoint expects an ID and returns no content
        restTemplate.postForObject(venueApiUrl + "/venues/confirm/{id}", null, Void.class, reservationId);
    }

    public void cancel(String reservationId) {
        // The venue service's /cancel/{id} endpoint expects an ID and returns no content
        restTemplate.postForObject(venueApiUrl + "/venues/cancel/{id}", null, Void.class, reservationId);
    }
}