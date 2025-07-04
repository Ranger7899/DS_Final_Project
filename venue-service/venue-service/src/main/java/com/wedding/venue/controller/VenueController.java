package com.wedding.venue.controller;

import com.wedding.venue.model.Reservation; //
import com.wedding.venue.model.ReserveRequest; //
import com.wedding.venue.model.Venue; //
import com.wedding.venue.service.VenueService; //
import org.springframework.beans.factory.annotation.Autowired; //
import org.springframework.http.HttpStatus; //
import org.springframework.http.ResponseEntity; //
import org.springframework.web.bind.annotation.*; //

import java.util.List; //

@RestController //
@RequestMapping("/venues") // Base path for venue endpoints
@CrossOrigin(origins = "http://localhost:8082") // Add this line with the origin of your home.html
public class VenueController {

    @Autowired
    private VenueService venueService; //

    @GetMapping("/available")
    public ResponseEntity<List<Venue>>getAvailableVenues(
            @RequestParam String date,
            @RequestParam String location) { //
        // Example: /venues/available?date=2025-12-24&location=New%20York
        List<Venue> venues = venueService.getAvailableVenues(date, location);
        if(venues== null || venues.isEmpty()){
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(venues); // 200 OK
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveVenue(@RequestBody ReserveRequest request) { //
        try {
            Reservation reservation = venueService.reserveVenue( //
                    request.getVenueId(), // Now a Long
                    request.getDate(),    // Now a LocalDate
                    request.getLocation()
            );
            return ResponseEntity.ok(reservation); //
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //
        }
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<?> confirmReservation(@PathVariable Long id) { // Changed ID type to Long
        try {
            venueService.confirmReservation(id); //
            return ResponseEntity.ok().build(); //
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //
        }
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) { // Changed ID type to Long
        try {
            venueService.cancelReservation(id); //
            return ResponseEntity.ok().build(); //
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVenueById(@PathVariable Long id) { //
        try {
            Venue venue = venueService.getVenueById(id); //
            return ResponseEntity.ok(venue); //
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //
        }
    }
    @GetMapping("/reservation/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id){
        try{
            Reservation reservation = venueService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //
        }
    }

    // NEW: Endpoint to get all distinct locations
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getAllLocations() {
        List<String> locations = venueService.getAllDistinctLocations();
        if(locations == null || locations.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(locations);
    }
}