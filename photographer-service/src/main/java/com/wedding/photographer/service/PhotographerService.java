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

    public Reservation reservePhotographer(Long photoId, LocalDate date, String location){
        Optional<Photographer> optionalPhotographer = photographerRepository.findById(photoId);

        if(optionalPhotographer.isEmpty()){
            throw new RuntimeException("Photographer with Id"+ photoId +" not found.");
        }
        Photographer photographer = optionalPhotographer.get();
        if(!photographer.isAvailable()){
            throw new RuntimeException("Photographer " + photographer.getName() + "is not generally available. ");
        }
        List<Reservation> existingReservations = reservationRepository.findByPhotoIdAndDateAndStatusIn(photoId, date, Arrays.asList("pending", "confirmed"));

        if(!existingReservations.isEmpty()){
            throw new RuntimeException("Photographer" + photographer.getName() + " is already reserved for " + date +".");
        }

        Reservation reservation = new Reservation(
                photoId,
                date,
                location,
                "pending"
        );
        return reservationRepository.save(reservation);
    }

    public void confirmReservation(Long reservationId){
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        if(optionalReservation.isPresent()){
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("confirmed");
            reservationRepository.save(reservation);
        }else{
            throw new RuntimeException("Reservation with ID " + reservationId + " not found.");
        }
    }
    public void cancelReservation(Long reservationId){
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()){
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("cancelled");
            reservationRepository.save(reservation);
        }else{
            throw new RuntimeException("Reservation with ID " + reservationId + " not found.");

        }
    }

    // NEW: Method to get distinct locations
    public List<String> getAllDistinctLocations() {
        return photographerRepository.findDistinctLocationsOfAvailablePhotographers();
    }
}
