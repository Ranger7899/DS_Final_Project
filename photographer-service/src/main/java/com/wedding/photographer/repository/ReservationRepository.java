package com.wedding.photographer.repository;

import com.wedding.photographer.model.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> { // Extend JpaRepository and specify entity and ID type
    List<Reservation> findByPhotoIdAndDateAndStatusIn(Long photoId, LocalDate date, List<String> statuses);

    List<Reservation> findByCreatedBefore(LocalDateTime timeoutTime); //TODO: delete this if necessary

    @Transactional
    @Modifying
    @Query(" DELETE FROM Reservation r " +
            "WHERE r.status = 'pending' AND r.created < :timeoutTime")
    void deleteOldPendingReservations(LocalDateTime timeoutTime);
}