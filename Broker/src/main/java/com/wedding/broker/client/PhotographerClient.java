package com.wedding.broker.client;

import com.wedding.broker.model.Photographer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
        return restTemplate.exchange(
                photographerApiUrl + "/photographers/available?date={date}&location={location}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Photographer>>() {},
                date, location
        ).getBody();
    }
}