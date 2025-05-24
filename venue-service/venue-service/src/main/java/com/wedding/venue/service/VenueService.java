// src/main/java/com/wedding/venue/service/VenueService.java
package com.wedding.venue.service;

import com.wedding.venue.model.Reservation;
import com.wedding.venue.model.Venue;
import com.wedding.venue.repository.ReservationRepository;
import com.wedding.venue.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Venue> getAvailableVenues(String dateString, String location) {
        LocalDate date = LocalDate.parse(dateString); // Parse date string to LocalDate
        return venueRepository.findAvailableVenues(date, location);
    }

    public Reservation reserveVenue(String venueId, String dateString, String location, int timeout) {
        LocalDate date = LocalDate.parse(dateString); // Parse date string to LocalDate
        Optional<Venue> optionalVenue = venueRepository.findById(venueId);

        if (optionalVenue.isEmpty()) {
            throw new RuntimeException("Venue with ID " + venueId + " not found.");
        }

        Venue venue = optionalVenue.get();

        // First, check if the venue is generally available
        if (!venue.isAvailable()) {
            throw new RuntimeException("Venue with ID " + venueId + " is not generally available.");
        }

        // Now, check if there's an existing reservation for this venue on this date
        List<Reservation> existingReservations = reservationRepository.findByDate(dateString);
        boolean isAlreadyReserved = existingReservations.stream()
                .anyMatch(r -> r.getVenueId().equals(venueId) &&
                        r.getStatus().equals("pending") || r.getStatus().equals("confirmed"));

        if (isAlreadyReserved) {
            throw new RuntimeException("Venue with ID " + venueId + " is already reserved for " + dateString + ".");
        }

        // Create and save the reservation
        Reservation reservation = new Reservation(
                "res-" + ThreadLocalRandom.current().nextInt(1000, 9999), // Dummy ID
                venueId,
                dateString, // Store date as string in Reservation
                location,
                "pending"
        );
        reservationRepository.save(reservation);
        return reservation;
    }

    public void confirmReservation(String reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("confirmed");
            reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Reservation with ID " + reservationId + " not found.");
        }
    }

    public void cancelReservation(String reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("cancelled");
            reservationRepository.save(reservation);
            // No need to change venue availability here, as venue availability is general, not date-specific.
            // The `findAvailableVenues` method will now implicitly exclude cancelled reservations.
        } else {
            throw new RuntimeException("Reservation with ID " + reservationId + " not found.");
        }
    }
}