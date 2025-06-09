package com.wedding.photographer.service;
import com.wedding.photographer.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationScheduledTask {

    @Autowired
    private ReservationRepository reservationRepository;

    @Scheduled(fixedRate = 60000)
    public void timeoutPendingReservations(){
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(15);
        reservationRepository.deleteOldPendingReservations(timeoutTime);
    }
}
