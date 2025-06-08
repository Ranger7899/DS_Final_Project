package com.wedding.photographer.repository;
import com.wedding.photographer.model.Photographer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhotographerRepository extends JpaRepository<Photographer, Long>{
    @Query("SELECT p FROM Photographer p WHERE p.available = true AND p.location = :location AND p.id NOT IN (" +
            "SELECT r.photoId FROM Reservation r WHERE r.date = :date AND (r.status = 'pending' OR r.status = 'confirmed'))")
    List<Photographer> findAvailablePhotographers(LocalDate date, String location);

    // NEW: Custom query to find all distinct locations of available venues
    @Query("SELECT DISTINCT p.location FROM Photographer p")
    List<String> findDistinctLocationsOfAvailablePhotographers();
}
