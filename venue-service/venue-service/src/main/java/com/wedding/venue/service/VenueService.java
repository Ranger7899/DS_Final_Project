package com.wedding.venue.service;

import com.wedding.venue.model.Reservation;
import com.wedding.venue.model.Venue;
import com.wedding.venue.repository.ReservationRepository;
import com.wedding.venue.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays; // Import Arrays for List.of()
import java.util.List;
import java.util.Optional;

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

    public Venue getVenueById(Long venueId) {
        Optional<Venue> optionalVenue = venueRepository.findById(venueId);
        if (optionalVenue.isPresent()) {
            return optionalVenue.get();
        } else {
            throw new RuntimeException("Venue with ID " + venueId + " not found.");
        }
    }

    public Reservation reserveVenue(Long venueId, LocalDate date, String location, int timeout) {
        Optional<Venue> optionalVenue = venueRepository.findById(venueId);

        if (optionalVenue.isEmpty()) {
            throw new RuntimeException("Venue with ID " + venueId + " not found.");
        }

        Venue venue = optionalVenue.get();

        // Check if the venue is generally available
        if (!venue.isAvailable()) {
            throw new RuntimeException("Venue '" + venue.getName() + "' is not generally available.");
        }

        // Check for existing 'pending' or 'confirmed' reservations for this venue on this date
        List<Reservation> existingReservations = reservationRepository.findByVenueIdAndDateAndStatusIn(
                venueId, date, Arrays.asList("pending", "confirmed"));

        if (!existingReservations.isEmpty()) {
            throw new RuntimeException("Venue '" + venue.getName() + "' is already reserved for " + date + ".");
        }

        // Create a new reservation
        Reservation reservation = new Reservation(
                venueId,
                date,
                location,
                "pending" // Initial status
        );
        return reservationRepository.save(reservation);
    }

    public void confirmReservation(Long reservationId) { // Changed ID type to Long
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("confirmed");
            reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Reservation with ID " + reservationId + " not found.");
        }
    }

    public void cancelReservation(Long reservationId) { // Changed ID type to Long
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