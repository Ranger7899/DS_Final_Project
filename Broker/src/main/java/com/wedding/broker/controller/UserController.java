// src/main/java/com/wedding/broker/controller/UserController.java
package com.wedding.broker.controller;

import com.wedding.broker.model.OrderRequest;
import com.wedding.broker.service.OrderService;
import com.wedding.broker.client.VenueClient; // Import VenueClient
import com.wedding.broker.model.Venue; // Import Venue model
import com.wedding.broker.client.PhotographerClient; // Import PhotographerClient
import com.wedding.broker.model.Photographer; // Import Photographer model
import com.wedding.broker.model.Reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List; // Import List
import java.util.concurrent.TimeUnit;

@Controller
public class UserController {
    @Autowired
    private OrderService orderService;

    @Value("${venue.api.url}") // Inject the value from application.properties
    private String appBaseUrl;

    @Value("${photographer.api.url}") // Inject the value from application.properties
    private String photographerBaseUrl;

    @Autowired // Autowire VenueClient
    private VenueClient venueClient;

    @Autowired // Autowire PhotographerClient
    private PhotographerClient photographerClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${venue.service.api.base-url}")
    private String venueServiceApiBaseUrl;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("venueServiceApiBaseUrl", venueServiceApiBaseUrl);
        model.addAttribute("appBaseUrl", appBaseUrl);
        return "home";
    }

    @GetMapping("/services")
    public String services(@RequestParam String date, @RequestParam String location, Model model) {

        model.addAttribute("appBaseUrl", appBaseUrl);
        model.addAttribute("photographerBaseUrl", photographerBaseUrl);
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
            return "services";
        }

        // Query suppliers
        List<Venue> venues;
        List<Photographer> photographers;
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

        // Add attributes to model
        model.addAttribute("date", parsedDate);
        model.addAttribute("location", location);
        model.addAttribute("venues", venues);
        model.addAttribute("photographers", photographers);

        return "services";
    }

    // @GetMapping("/services")
    // public String services(@RequestParam String date, @RequestParam String location, Model model) {
    //     // Query venue service for available venues
    //     List<Venue> availableVenues = venueClient.getAvailableVenues(date, location);

    //     model.addAttribute("date", date);
    //     model.addAttribute("location", location);
    //     model.addAttribute("venues", availableVenues); // Add available venues to the model
    //     return "services";
    // }

    @GetMapping("/confirm")
    public String booking(@RequestParam(required = false) String venueId,
                        @RequestParam(required = false) String photographerId,
                        @RequestParam String date,
                        @RequestParam String location,
                        Model model) {

        Venue venue = null;
        Photographer photographer = null;
        int totalPrice = 0;
        String reservationId = null;

        if (venueId != null && !venueId.isEmpty()) {
            // Reserve venue
            // Reservation reservation = venueClient.reserve(venueId, date, location, 30); // 30-minute timeout
            // reservationId = reservation.getId();
            // Fetch venue details (assuming a method to get venue by ID)
            venue = venueClient.getVenueById(venueId); // Implement this in VenueClient
            totalPrice += venue.getPrice();
        }

        if (photographerId != null && !photographerId.isEmpty()) {
            // Dummy photographer data (replace with actual service when ready)
            photographer = getDummyPhotographer(photographerId);
            totalPrice += photographer.getPrice();
        }

        model.addAttribute("venue", venue);
        model.addAttribute("photographer", photographer);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("date", date);
        model.addAttribute("location", location);
        model.addAttribute("reservationId", reservationId);

        return "confirm";
    }

    // Temporary dummy method for photographer
    private Photographer getDummyPhotographer(String id) {
        // Replace with actual logic when photographer service is ready
        return new Photographer(id, "Photographer " + id, "/images/photographer.jpg", 1000, "Modern", 4.5, true);
    }

    // @PostMapping("/order")
    // public String placeOrder(OrderRequest orderRequest) {
    //     try {
    //         orderService.placeOrder(orderRequest);
    //         return "redirect:/confirmation"; // Redirect to a confirmation page on success
    //     } catch (RuntimeException e) {
    //         // Handle reservation failure, e.g., redirect to an error page or show a message
    //         return "redirect:/error?message=" + e.getMessage();
    //     }
    // }

    @PostMapping("/order")
    public String placeOrder(@ModelAttribute OrderRequest orderRequest, Model model) {
        try {
            // Process the order (e.g., save to database, handle payment)
            // orderService.placeOrder(orderRequest);

            Venue venue = null;
            Photographer photographer = null;
            int totalPrice = 0;

            // Fetch venue details and confirm reservation if venueId is provided
            if (orderRequest.getVenueId() != null && !orderRequest.getVenueId().isEmpty()) {
                venue = venueClient.getVenueById(orderRequest.getVenueId());
                totalPrice += venue.getPrice();
                // if (orderRequest.getReservationId() != null && !orderRequest.getReservationId().isEmpty()) {
                //     venueClient.confirm(orderRequest.getReservationId());
                // }
            }

            // Fetch photographer details if photographerId is provided
            if (orderRequest.getPhotographerId() != null && !orderRequest.getPhotographerId().isEmpty()) {
                // Placeholder: Replace with actual photographer service call when available
                photographer = getDummyPhotographer(orderRequest.getPhotographerId());
                totalPrice += photographer.getPrice();
            }

            // Add attributes to model for complete.html
            model.addAttribute("venue", venue);
            model.addAttribute("photographer", photographer);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("date", orderRequest.getDate());
            model.addAttribute("location", orderRequest.getLocation());
            // model.addAttribute("address", orderRequest.getAddress());
            // model.addAttribute("paymentDetails", orderRequest.getPaymentDetails());

            return "complete"; // Render complete.html
        } catch (RuntimeException e) {
            // Redirect to error page with message
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @GetMapping("/error")
    public String error(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("errorMessage", message != null ? message : "An unknown error occurred.");
        return "error"; // You might want to create a simple error.html page
    }
}