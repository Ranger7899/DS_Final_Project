package com.wedding.venue.repository;

import com.wedding.venue.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> { // Extend JpaRepository and specify entity and ID type

    // Custom method to find reservations by venue ID, date, and a list of statuses.
    // This will be useful for checking if a venue is already reserved on a specific date.
    List<Reservation> findByVenueIdAndDateAndStatusIn(Long venueId, LocalDate date, List<String> statuses);

    // You can also add more specific finder methods if needed, e.g.,
    // Optional<Reservation> findById(Long id); // Already provided by JpaRepository
    // List<Reservation> findByVenueId(Long venueId);
}