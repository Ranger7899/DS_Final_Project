// src/main/java/com/wedding/broker/controller/UserController.java
package com.wedding.broker.controller;

import com.wedding.broker.model.OrderRequest;
import com.wedding.broker.service.OrderService;
import com.wedding.broker.client.VenueClient; // Import VenueClient
import com.wedding.broker.model.Venue; // Import Venue model
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List; // Import List

@Controller
public class UserController {
    @Autowired
    private OrderService orderService;

    @Autowired // Autowire VenueClient
    private VenueClient venueClient;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/services")
    public String services(@RequestParam String date, @RequestParam String location, Model model) {
        // Query venue service for available venues
        List<Venue> availableVenues = venueClient.getAvailableVenues(date, location);

        model.addAttribute("date", date);
        model.addAttribute("location", location);
        model.addAttribute("venues", availableVenues); // Add available venues to the model
        return "services";
    }

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