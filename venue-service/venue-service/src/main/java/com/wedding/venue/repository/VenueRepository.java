package com.wedding.venue.repository;

import com.wedding.venue.model.Venue;
import com.wedding.venue.model.Reservation; // Import Reservation
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired; // Autowire ReservationRepository

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.UUID;

@Repository
public class VenueRepository {
    private final Map<String, Venue> venues = new ConcurrentHashMap<>();

    @Autowired
    private ReservationRepository reservationRepository; // Autowire ReservationRepository

    public VenueRepository() {
        // Initialize with some dummy venues for testing
        // Venues are now generally available, not date-specific in their definition
        Venue venue1 = new Venue(UUID.randomUUID().toString(), "Grand Ballroom", "New York", true);
        Venue venue2 = new Venue(UUID.randomUUID().toString(), "Crystal Hall", "Los Angeles", true);
        Venue venue3 = new Venue(UUID.randomUUID().toString(), "Riverside Place", "New York", true);
        Venue venue4 = new Venue(UUID.randomUUID().toString(), "Historic Mansion", "London", true);

        venues.put(venue1.getId(), venue1);
        venues.put(venue2.getId(), venue2);
        venues.put(venue3.getId(), venue3);
        venues.put(venue4.getId(), venue4);

        // A venue that is generally unavailable (e.g., undergoing renovation)
        Venue unavailableVenue = new Venue(UUID.randomUUID().toString(), "Unavailable Hall", "New York", false);
        venues.put(unavailableVenue.getId(), unavailableVenue);
    }

    public List<Venue> findAll() {
        return new ArrayList<>(venues.values());
    }

    public Optional<Venue> findById(String id) {
        return Optional.ofNullable(venues.get(id));
    }

    public List<Venue> findAvailableVenues(LocalDate date, String location) {
        // Get all venues that are generally available and in the specified location
        List<Venue> potentialVenues = venues.values().stream()
                .filter(venue -> venue.isAvailable() &&
                        venue.getLocation().equalsIgnoreCase(location))
                .collect(Collectors.toList());

        // Filter out venues that have existing 'confirmed' or 'pending' reservations for the given date
        List<Reservation> reservationsForDate = reservationRepository.findByDate(date.toString()); // Assuming date is stored as string in Reservation
        List<String> reservedVenueIds = reservationsForDate.stream()
                .filter(r -> r.getStatus().equals("confirmed") || r.getStatus().equals("pending"))
                .map(Reservation::getVenueId)
                .collect(Collectors.toList());

        return potentialVenues.stream()
                .filter(venue -> !reservedVenueIds.contains(venue.getId()))
                .collect(Collectors.toList());
    }

    public Venue save(Venue venue) {
        if (venue.getId() == null) {
            venue.setId(UUID.randomUUID().toString());
        }
        venues.put(venue.getId(), venue);
        return venue;
    }

    public void deleteById(String id) {
        venues.remove(id);
    }
}