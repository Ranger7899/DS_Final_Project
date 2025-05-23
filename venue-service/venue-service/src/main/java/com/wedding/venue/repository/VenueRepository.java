package com.wedding.venue.repository;

import com.wedding.venue.model.Venue;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.UUID; // For generating unique IDs

@Repository
public class VenueRepository {
    private final Map<String, Venue> venues = new ConcurrentHashMap<>();

    public VenueRepository() {
        // Initialize with some dummy venues for testing
        // Make some venues available
        Venue venue1 = new Venue(UUID.randomUUID().toString(), "Grand Ballroom", "New York", LocalDate.of(2025, 12, 24), true);
        Venue venue2 = new Venue(UUID.randomUUID().toString(), "Crystal Hall", "Los Angeles", LocalDate.of(2025, 12, 24), true);
        Venue venue3 = new Venue(UUID.randomUUID().toString(), "Riverside Place", "New York", LocalDate.of(2025, 12, 25), true);
        Venue venue4 = new Venue(UUID.randomUUID().toString(), "Historic Mansion", "London", LocalDate.of(2025, 12, 24), true);

        venues.put(venue1.getId(), venue1);
        venues.put(venue2.getId(), venue2);
        venues.put(venue3.getId(), venue3);
        venues.put(venue4.getId(), venue4);

        // Make one venue unavailable for a specific date/location combination
        Venue unavailableVenue = new Venue(UUID.randomUUID().toString(), "Unavailable Hall", "New York", LocalDate.of(2025, 12, 24), false);
        venues.put(unavailableVenue.getId(), unavailableVenue);
    }

    public List<Venue> findAll() {
        return new ArrayList<>(venues.values());
    }

    public Optional<Venue> findById(String id) {
        return Optional.ofNullable(venues.get(id));
    }

    public List<Venue> findAvailableVenues(LocalDate date, String location) {
        return venues.values().stream()
                .filter(venue -> venue.isAvailable() &&
                        venue.getDate().equals(date) &&
                        venue.getLocation().equalsIgnoreCase(location))
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