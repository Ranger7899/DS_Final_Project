package com.wedding.venue.repository;

import com.wedding.venue.model.Reservation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.stream.Collectors; // Import for stream operations

@Repository
public class ReservationRepository {
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(reservations.get(id));
    }

    public List<Reservation> findByDate(String date) {
        return reservations.values().stream()
                .filter(reservation -> reservation.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(UUID.randomUUID().toString());
        }
        reservations.put(reservation.getId(), reservation);
        return reservation;
    }

    public void deleteById(String id) {
        reservations.remove(id);
    }
}