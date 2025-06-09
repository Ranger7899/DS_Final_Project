package com.wedding.catering.repository;

import com.wedding.catering.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Find reservations by company and date where status is pending or confirmed
    List<Reservation> findByCateringCompanyIdAndDateAndStatusIn(Long cateringCompanyId, LocalDate date, List<String> statuses);

}
