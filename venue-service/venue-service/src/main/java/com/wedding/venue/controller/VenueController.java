package com.wedding.venue.controller;

import com.wedding.venue.model.Reservation;
import com.wedding.venue.model.ReserveRequest;
import com.wedding.venue.model.Venue;
import com.wedding.venue.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues") // Base path for venue endpoints
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping("/available")
    public List<Venue> getAvailableVenues(@RequestParam String date, @RequestParam String location) {
        // Example: /venues/available?date=2025-12-24&location=New%20York
        return venueService.getAvailableVenues(date, location);
    }

    @PostMapping("/reserve")
    public ResponseEntity<Reservation> reserveVenue(@RequestBody ReserveRequest request) {
        Reservation reservation = venueService.reserveVenue(
                request.getVenueId(),
                request.getDate(),
                request.getLocation(),
                request.getTimeout()
        );
        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        }
        return ResponseEntity.badRequest().build(); // Or a more specific error
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<Void> confirmReservation(@PathVariable String id) {
        venueService.confirmReservation(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        venueService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }
}