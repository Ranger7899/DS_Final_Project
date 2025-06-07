package com.wedding.broker.client;

import com.wedding.broker.model.Photographer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class PhotographerClient {
    // private final RestTemplate restTemplate;
    // private final String photographerApiUrl;

    // public PhotographerClient(RestTemplate restTemplate, @Value("${photographer.api.url}") String photographerApiUrl) {
    //     this.restTemplate = restTemplate;
    //     this.photographerApiUrl = photographerApiUrl;
    // }

    // public List<Photographer> getAvailablePhotographers(String date, String location) {
    //     return restTemplate.exchange(
    //             photographerApiUrl + "/photographers/available?date={date}&location={location}",
    //             HttpMethod.GET,
    //             null,
    //             new ParameterizedTypeReference<List<Photographer>>() {},
    //             date, location
    //     ).getBody();
    // }

    public List<Photographer> getAvailablePhotographers(String date, String location) {
        // Hardcoded photographer data for testing
        List<Photographer> photographers = Arrays.asList(
            new Photographer(
                "p1",
                "Pierre Photography",
                "/images/photographers/pierre.jpg",
                1500,
                "Candid",
                4.8,
                true
            ),
            new Photographer(
                "p2",
                "Sophie Snaps",
                "/images/photographers/sophie.jpg",
                2000,
                "Cinematic",
                4.6,
                true
            )
        );
        return photographers;
    }
}