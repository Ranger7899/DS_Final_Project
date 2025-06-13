// src/main/java/com/wedding/broker/controller/UserController.java
package com.wedding.broker.controller;

import com.wedding.broker.client.CateringClient;
import com.wedding.broker.model.*;
import com.wedding.broker.client.VenueClient; // Import VenueClient
import com.wedding.broker.client.PhotographerClient; // Import PhotographerClient

import com.wedding.broker.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List; // Import List
import java.util.Optional;


@Controller
public class UserController {
    @Value("${venue.api.url}") // Inject the value from application.properties
    private String appBaseUrl;

    @Value("${photographer.api.url}") // Inject the value from application.properties
    private String photographerBaseUrl;

    @Value("${catering.api.url}")
    private String cateringBaseUrl;

    @Autowired // Autowire VenueClient
    private VenueClient venueClient;

    @Autowired // Autowire PhotographerClient
    private PhotographerClient photographerClient;

    @Autowired
    private CateringClient cateringClient;

    @Autowired
    private OrderRepository orderRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${venue.service.api.base-url}")
    private String venueServiceApiBaseUrl;

    @Value("${photographer.service.api.base-url}")
    private String photographerServiceApiBaseUrl;

    @Value("${catering.service.api.base-url}")
    private String cateringServiceApiBaseUrl;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("venueServiceApiBaseUrl", venueServiceApiBaseUrl);
        model.addAttribute("photographerServiceApiBaseUrl", photographerServiceApiBaseUrl);
        model.addAttribute("cateringServiceApiBaseUrl", cateringServiceApiBaseUrl);
      
        model.addAttribute("appBaseUrl", appBaseUrl);
        return "home";
    }

    @GetMapping("/services")
    public String services(@RequestParam String date, @RequestParam String location, Model model) {

        model.addAttribute("appBaseUrl", appBaseUrl);
        model.addAttribute("photographerBaseUrl", photographerBaseUrl);
        model.addAttribute("cateringBaseUrl", cateringBaseUrl);
        // Parse date to LocalDate
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date, DATE_FORMATTER);
        } catch (Exception e) {
            model.addAttribute("error", "Invalid date format. Please use YYYY-MM-DD.");
            model.addAttribute("date", date);
            model.addAttribute("location", location);
            model.addAttribute("venues", Collections.emptyList());
            model.addAttribute("photographers", Collections.emptyList());
            model.addAttribute("caterings", Collections.emptyList());
            return "services";
        }

        // Query suppliers
        List<Venue> venues;
        List<Photographer> photographers;
        List<Catering> caterings;
        try {
            venues = venueClient.getAvailableVenues(date, location);
        } catch (Exception e) {
            venues = Collections.emptyList();
        }
        try {
            photographers = photographerClient.getAvailablePhotographers(date, location);
        } catch (Exception e) {
            photographers = Collections.emptyList();
        }
        try {
            caterings = cateringClient.getAvailableCompanies(date, location);
        }catch (Exception e){
            caterings = Collections.emptyList();
        }

        // Add attributes to model
        model.addAttribute("date", parsedDate);
        model.addAttribute("location", location);
        model.addAttribute("venues", venues);
        model.addAttribute("photographers", photographers);
        model.addAttribute("caterings", caterings);

        return "services";
    }

    @GetMapping("/confirm")
    public String booking(@RequestParam(required = false) String venueId,
        @RequestParam(required = false) String photographerId,
        @RequestParam(required = false) String cateringId,
        @RequestParam(required = false) String venueReservationId,
        @RequestParam(required = false) String photographerReservationId,
        @RequestParam(required = false) String cateringReservationId,
        @RequestParam String date,
        @RequestParam String location,
        Model model) {

        model.addAttribute("appBaseUrl", appBaseUrl);
        model.addAttribute("photographerBaseUrl", photographerBaseUrl);
        model.addAttribute("cateringBaseUrl", cateringBaseUrl);

        Venue venue = null;
        Photographer photographer = null;
        Catering catering = null;
        int totalPrice = 0;
        ErrorOrderMessage errorOrderMessage = new ErrorOrderMessage();
        //get before
        try{
            if(venueReservationId != null && !venueReservationId.trim().isEmpty()){
                venue = venueClient.getVenueById(venueId);
            }
        } catch (Exception e) {
            errorOrderMessage.setErrorTrue();
            errorOrderMessage.addToMessage("Venue");
            model.addAttribute("error", errorOrderMessage.getMessage());
        }
        try{
            if(photographerId != null  && !photographerId.trim().isEmpty()) {
                photographer = photographerClient.getPhotographerById(photographerId);
            }
        } catch (Exception e) {
            errorOrderMessage.setErrorTrue();
            errorOrderMessage.addToMessage("Photographer");
            model.addAttribute("error", errorOrderMessage.getMessage());
        }
        try{
            if(cateringReservationId != null && !cateringReservationId.trim().isEmpty()){
                catering= cateringClient.getCateringCompanyById(cateringId);
            }
        } catch (Exception e) {
            errorOrderMessage.setErrorTrue();
            errorOrderMessage.addToMessage("Catering Company");
            model.addAttribute("error", errorOrderMessage.getMessage());
        }
        if(errorOrderMessage.isError()){
            model.addAttribute("venueId", venueId);
            model.addAttribute("photographerId", photographerId);
            model.addAttribute("cateringId", cateringId);
            model.addAttribute("venueReservationId", venueReservationId);
            model.addAttribute("photographerReservationId", photographerReservationId);
            model.addAttribute("cateringReservationId", cateringReservationId);
            return "error";
        }

        // Reserve Venue
        if (venueReservationId != null && venueId != null && !venueId.trim().isEmpty()) { //Handle if someone continuing their reservation from the error page
            venue = venueClient.getVenueById(venueId);
            totalPrice += venue.getPrice();
        } else if (venueId != null && !venueId.trim().isEmpty()) {
            try {
                VenueReservation reservationVenue = venueClient.reserve(venueId, date, location);
                venueReservationId = reservationVenue.getId();
                totalPrice += venue.getPrice();
            } catch (HttpClientErrorException e) {
                venueReservationId = null;
                venueId = null;
                errorOrderMessage.setErrorTrue();
                errorOrderMessage.addToMessage("Venue");
                model.addAttribute("error", errorOrderMessage.getMessage());
            }
        }

        // Reserve Photographer
        if (photographerReservationId != null && photographerId != null && !photographerId.trim().isEmpty()) { //Handle if someone continuing their reservation from the error page
            photographer = photographerClient.getPhotographerById(photographerId);
            totalPrice += photographer.getPrice();
        } else if (photographerId != null && !photographerId.trim().isEmpty()) {
            try {
                PhotographerReservation reservationPhotographer = photographerClient.reserve(photographerId, date, location);
                photographerReservationId = reservationPhotographer.getId();
                totalPrice += photographer.getPrice();
            } catch (HttpClientErrorException e) {
                photographerReservationId = null;
                photographerId = null;
                errorOrderMessage.setErrorTrue();
                errorOrderMessage.addToMessage("Photographer");
                model.addAttribute("error", errorOrderMessage.getMessage());
            }
        }

        // Reserve Catering
        if (cateringReservationId != null && cateringId != null && !cateringId.trim().isEmpty()) { //Handle if someone continuing their reservation from the error page
            totalPrice += catering.getPrice();
        } else if (cateringId != null && !cateringId.trim().isEmpty()) {
            try {
                CateringReservation reservationCatering = cateringClient.reserve(cateringId, date, location);
                cateringReservationId = reservationCatering.getId();
                totalPrice += catering.getPrice();
            } catch (HttpClientErrorException e) {
                cateringReservationId = null;
                cateringId = null;
                errorOrderMessage.setErrorTrue();
                errorOrderMessage.addToMessage("Catering");
                model.addAttribute("error", errorOrderMessage.getMessage());
            }
        }


        model.addAttribute("venueReservationId", venueReservationId);
        model.addAttribute("photographerReservationId", photographerReservationId);
        model.addAttribute("cateringReservationId", cateringReservationId);
        model.addAttribute("venue", venue);
        model.addAttribute("photographer", photographer);
        model.addAttribute("catering", catering);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("date", date);
        model.addAttribute("location", location);

        if(errorOrderMessage.isError()){
            model.addAttribute("venueId", venueId);
            model.addAttribute("photographerId", photographerId);
            model.addAttribute("cateringId", cateringId);
            model.addAttribute("venueReservationId", venueReservationId);
            model.addAttribute("photographerReservationId", photographerReservationId);
            model.addAttribute("cateringReservationId", cateringReservationId);
            return "error";
        }
        return "confirm";
    }

    @PostMapping("/cancel-reservations")
    public String cancelReservations(@RequestParam(required = false) String venueReservationId,
                                    @RequestParam(required = false) String photographerReservationId,
                                    @RequestParam(required = false) String cateringReservationId,
                                     @RequestParam(required = false) String orderId) {

        VenueReservation venueReservation = null;
        PhotographerReservation photographerReservation = null;
        CateringReservation cateringReservation = null;
        //Check connection to suppliers works before cancelling
        try{
            if(photographerReservationId != null  && !photographerReservationId.trim().isEmpty()) {
                photographerReservation = photographerClient.getPhotographerReservationById(photographerReservationId);
            }
            if(venueReservationId != null && !venueReservationId.trim().isEmpty()){
                venueReservation = venueClient.getVenueReservationById(venueReservationId);
            }
            if(cateringReservationId != null && !cateringReservationId.trim().isEmpty()){
                cateringReservation = cateringClient.getCateringReservationById(cateringReservationId);
            }
        }catch (Exception e){
            return "redirect:/errorcancel?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
        //cancel orders when they are all ok
        try {
            if (venueReservationId != null && !venueReservationId.trim().isEmpty()) {
                venueClient.cancel(venueReservationId);
            }
            if (photographerReservationId != null && !photographerReservationId.trim().isEmpty()) {
                photographerClient.cancel(photographerReservationId);
            }
            if (cateringReservationId != null && !cateringReservationId.trim().isEmpty()) {
                cateringClient.cancel(cateringReservationId);
            }
        } catch (Exception e) {
            return "redirect:/errorcancel?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
        //change order from Confirmed to Cancelled in broker dB
        if(orderId != null){
            try {
                Optional<Order> orderOptional = orderRepository.findById(Long.valueOf(orderId));
                if (orderOptional.isPresent()) {
                    Order order = orderOptional.get();
                    order.setStatus("Cancelled");
                    orderRepository.save(order);
                }
            }catch (Exception e){
                return "redirect:/errorcancel?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            }
        }
        return "redirect:/";
    }


    @GetMapping("/error")
    public String error(@RequestParam(required = false) String message,
                        @RequestParam(required = false) String venueReservationId,
                        @RequestParam(required = false) String photographerReservationId,
                        @RequestParam(required = false) String cateringReservationId,
                        @RequestParam(required = false) String venueId,
                        @RequestParam(required = false) String photographerId,
                        @RequestParam(required = false) String cateringId,
                        @RequestParam String date,
                        @RequestParam String location,
                        Model model) {
        model.addAttribute("errorMessage", message != null ? message : "An unknown error occurred.");
        model.addAttribute("venueReservationId", venueReservationId);
        model.addAttribute("photographerReservationId", photographerReservationId);
        model.addAttribute("cateringReservationId", cateringReservationId);
        model.addAttribute("venueId", venueId);
        model.addAttribute("photographerId", photographerId);
        model.addAttribute("cateringId", cateringId);
        model.addAttribute("date", date);
        model.addAttribute("location", location);

        return "error"; // You might want to create a simple error.html page
    }

    @GetMapping("/errorcancel")
    public String cancelError(@RequestParam String message,
                              Model model){
        model.addAttribute("errorMessage", message);
        return "errorcancel";

    }
}