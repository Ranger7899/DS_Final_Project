package com.wedding.photographer.contoller;

import com.wedding.photographer.model.Reservation;
import com.wedding.photographer.model.Photographer;
import com.wedding.photographer.model.ReserveRequest;
import com.wedding.photographer.service.PhotographerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/photographers")
public class PhotographerController {

    @Autowired
    private PhotographerService photographerService;

    @GetMapping("/available")
    public ResponseEntity<List<Photographer>> getAvailablePhotographers(
            @RequestParam String date,
            @RequestParam String location) {

        List<Photographer> photographers = photographerService.getAvailablePhotographers(date, location);

        if (photographers == null || photographers.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(photographers); // 200 OK + body
    }


    @PostMapping("/reserve")
    public ResponseEntity<?> reservePhotographers(@RequestBody ReserveRequest request) {
        try {
            Reservation reservation = photographerService.reservePhotographer(
                    request.getPhotoId(), // Now a Long
                    request.getDate(),    // Now a LocalDate
                    request.getLocation(),
                    request.getTimeout()
            );
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<?> confirmReservation(@PathVariable Long id) { // Changed ID type to Long
        try {
            photographerService.confirmReservation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) { // Changed ID type to Long
        try {
            photographerService.cancelReservation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
