// src/main/java/com/wedding/broker/controller/UserController.java
package com.wedding.broker.controller;

import com.wedding.broker.model.OrderRequest;
import com.wedding.broker.service.OrderService;
import com.wedding.broker.client.VenueClient; // Import VenueClient
import com.wedding.broker.model.Venue; // Import Venue model
import com.wedding.broker.client.PhotographerClient; // Import PhotographerClient
import com.wedding.broker.model.Photographer; // Import Photographer model
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List; // Import List
import java.util.concurrent.TimeUnit;

@Controller
public class UserController {
    @Autowired
    private OrderService orderService;

    @Autowired // Autowire VenueClient
    private VenueClient venueClient;

    @Autowired // Autowire PhotographerClient
    private PhotographerClient photographerClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/services")
    public String services(@RequestParam String date, @RequestParam String location, Model model) {
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

    @PostMapping("/order")
    public String placeOrder(OrderRequest orderRequest) {
        try {
            orderService.placeOrder(orderRequest);
            return "redirect:/confirmation"; // Redirect to a confirmation page on success
        } catch (RuntimeException e) {
            // Handle reservation failure, e.g., redirect to an error page or show a message
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @GetMapping("/confirmation")
    public String confirmation() {
        return "confirmation"; // You might want to create a simple confirmation.html page
    }

    @GetMapping("/error")
    public String error(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("errorMessage", message != null ? message : "An unknown error occurred.");
        return "error"; // You might want to create a simple error.html page
    }
}