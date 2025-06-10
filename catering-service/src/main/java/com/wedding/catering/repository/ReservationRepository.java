package com.wedding.catering.repository;

import com.wedding.catering.model.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Find reservations by company and date where status is pending or confirmed
    List<Reservation> findByCateringCompanyIdAndDateAndStatusIn(Long cateringCompanyId, LocalDate date, List<String> statuses);
    List<Reservation> findByCreatedBefore(LocalDateTime timeoutTime);

    @Transactional
    @Modifying
    @Query(" DELETE FROM Reservation r " +
            "WHERE r.status = 'pending' AND r.created < :timeoutTime")
    void deleteOldPendingReservations(LocalDateTime timeoutTime);

}
