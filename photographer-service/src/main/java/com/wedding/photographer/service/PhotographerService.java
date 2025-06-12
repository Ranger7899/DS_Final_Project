package com.wedding.photographer.service;

import com.wedding.photographer.model.Reservation;
import com.wedding.photographer.model.Photographer;
import com.wedding.photographer.repository.ReservationRepository;
import com.wedding.photographer.repository.PhotographerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PhotographerService {

    @Autowired
    private PhotographerRepository photographerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Photographer> getAvailablePhotographers(String dateString, String location){
        LocalDate date = LocalDate.parse(dateString);
        if (date.isBefore(LocalDate.now()) || date.isAfter(LocalDate.now().plusYears(2))) {
            throw new RuntimeException("Catering booking date: " + date + " is after or 2years before today: " + LocalDate.now());
        }
        return photographerRepository.findAvailablePhotographers(date, location);
    }

    public Photographer getPhotographerById(Long photographerID) { //
        Optional<Photographer> optionalPhotographer = photographerRepository.findById(photographerID); //
        if (optionalPhotographer.isPresent()) { //
            return optionalPhotographer.get(); //
        } else {
            throw new RuntimeException("Venue with ID " + photographerID + " not found."); //
        }
    }

    public Reservation getReservationById(Long reservationID){
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationID);
        if(optionalReservation.isPresent()){
            return optionalReservation.get();
        }else{
            throw new RuntimeException("Reserrvation " + reservationID + " not found.");
        }
    }

    public Reservation reservePhotographer(Long photoId, LocalDate date, String location) {
        if (date.isBefore(LocalDate.now()) || date.isAfter(LocalDate.now().plusYears(2))) {
            throw new RuntimeException("Photographer booking date: " + date + " is after or 2years before today: " + LocalDate.now());
        }
        Optional<Photographer> optionalPhotographer = photographerRepository.findById(photoId);
        if (optionalPhotographer.isEmpty()) {
            throw new RuntimeException("Photographer with Id" + photoId + " not found.");
        }
        Photographer photographer = optionalPhotographer.get();
        if (!photographer.isAvailable()) {
            throw new RuntimeException("Photographer " + photographer.getName() + "is not generally available. ");
        }
        List<Reservation> existingReservations = reservationRepository.findByPhotoIdAndDateAndStatusIn(photoId, date, Arrays.asList("pending", "confirmed"));

        if (!existingReservations.isEmpty()) {
            throw new RuntimeException("Photographer" + photographer.getName() + " is already reserved for " + date + ".");
        }

        Reservation reservation = new Reservation(
                photoId,
                date,
                location,
                "pending"
        );
        Reservation result;
        try {
            result = reservationRepository.save(reservation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to make pending reservation in database.", e);
        }
        return result;
    }

    public void confirmReservation(Long reservationId){
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        if(optionalReservation.isPresent()){
            Reservation reservation = optionalReservation.get();
            if("pending".equalsIgnoreCase(reservation.getStatus())){
                try{
                    reservation.setStatus("confirmed");
                    reservationRepository.save(reservation);
                } catch (Exception e){
                    throw new RuntimeException("Failed to confirm reservation in database.", e);
                }
            }else{
                throw new RuntimeException("Reservation with ID " + reservationId + " is not in a pending state.");
            }
        }else{
            throw new RuntimeException("Reservation with ID " + reservationId + " not found.");
        }
    }
    public void cancelReservation(Long reservationId){
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()){
            Reservation reservation = optionalReservation.get();
            try{
                reservation.setStatus("cancelled");
                reservationRepository.save(reservation);
            } catch (Exception e){
                throw new RuntimeException("Failed to cancel reservation in database.", e);
            }
        }else{
            throw new RuntimeException("Reservation with ID " + reservationId + " not found.");
        }
    }

    // NEW: Method to get distinct locations
    public List<String> getAllDistinctLocations() {
        return photographerRepository.findDistinctLocationsOfAvailablePhotographers();
    }
}
