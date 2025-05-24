package com.wedding.venue.repository;

import com.wedding.venue.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> { // Extend JpaRepository and specify entity and ID type

    // Custom query to find available venues for a specific date and location.
    // It selects venues that are generally available (venue.available = true)
    // and whose location matches the given location,
    // AND which do NOT have an existing 'pending' or 'confirmed' reservation for the given date.
    @Query("SELECT v FROM Venue v WHERE v.available = true AND v.location = :location AND v.id NOT IN (" +
            "SELECT r.venueId FROM Reservation r WHERE r.date = :date AND (r.status = 'pending' OR r.status = 'confirmed'))")
    List<Venue> findAvailableVenues(LocalDate date, String location);
}