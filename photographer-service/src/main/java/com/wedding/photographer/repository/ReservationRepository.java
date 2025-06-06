package com.wedding.photographer.repository;

import com.wedding.photographer.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> { // Extend JpaRepository and specify entity and ID type
    List<Reservation> findByPhotoIdAndDateAndStatusIn(Long photoId, LocalDate date, List<String> statuses);
}