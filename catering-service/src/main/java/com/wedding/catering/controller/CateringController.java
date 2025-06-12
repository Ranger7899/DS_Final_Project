package com.wedding.catering.controller;

import com.wedding.catering.model.CateringCompany;
import com.wedding.catering.model.Reservation;
import com.wedding.catering.model.ReserveRequest;
import com.wedding.catering.service.CateringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catering")
public class CateringController {

    @Autowired
    private CateringService cateringService;

    // Get available catering companies
    @GetMapping("/available")
    public ResponseEntity<List<CateringCompany>> getAvailableCompanies(
            @RequestParam String date,
            @RequestParam String location) {

        List<CateringCompany> companies = cateringService.getAvailableCompanies(date, location);

        if (companies == null || companies.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(companies); // 200 OK
    }

    // Reserve a catering company
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveCatering(@RequestBody ReserveRequest request) {
        try {
            Reservation reservation = cateringService.reserveCatering(
                    request.getCompanyId(),
                    request.getDate(),
                    request.getLocation()
            );
            return ResponseEntity.ok(reservation); // 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Confirm a reservation
    @PostMapping("/confirm/{id}")
    public ResponseEntity<?> confirmReservation(@PathVariable Long id) {
        try {
            cateringService.confirmReservation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Cancel a reservation
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            cateringService.cancelReservation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get company by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCateringCompanyById(@PathVariable Long id) {
        try {
            CateringCompany company = cateringService.getCateringCompanyById(id);
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id){
        try{
            Reservation reservation = cateringService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //
        }
    }

    // Get all distinct locations
    @GetMapping("/locations")
    public ResponseEntity<List<String>> getAllLocations() {
        List<String> locations = cateringService.getAllDistinctLocations();
        if (locations == null || locations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(locations);
    }
}
