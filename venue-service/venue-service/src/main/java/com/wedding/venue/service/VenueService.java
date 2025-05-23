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
import java.util.concurrent.ThreadLocalRandom; // For dummy reservation IDs

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

    public Reservation reserveVenue(String venueId, String date, String location, int timeout) {
        Optional<Venue> optionalVenue = venueRepository.findById(venueId);

        if (optionalVenue.isPresent() && optionalVenue.get().isAvailable()) {
            // Simulate reservation logic
            // In a real scenario, you'd mark the venue as unavailable or create a specific reservation slot
            Reservation reservation = new Reservation(
                    "res-" + ThreadLocalRandom.current().nextInt(1000, 9999), // Dummy ID
                    venueId,
                    date,
                    location,
                    "pending"
            );
            reservationRepository.save(reservation);
            return reservation;
        }
        return null; // Venue not found or not available
    }

    public void confirmReservation(String reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("confirmed");
            reservationRepository.save(reservation);
            // In a real system, you'd also update the venue's availability status
        }
    }

    public void cancelReservation(String reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("cancelled");
            reservationRepository.save(reservation);
            // In a real system, you'd also free up the venue's availability
        }
    }
}