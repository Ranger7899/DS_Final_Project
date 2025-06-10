package com.wedding.catering.service;

import com.wedding.catering.model.CateringCompany;
import com.wedding.catering.model.Reservation;
import com.wedding.catering.repository.CateringCompanyRepository;
import com.wedding.catering.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CateringService {

    @Autowired
    private CateringCompanyRepository cateringCompanyRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    // Fetch companies available for a specific date and location
    public List<CateringCompany> getAvailableCompanies(String dateString, String location) {
        LocalDate date = LocalDate.parse(dateString);

        List<CateringCompany> allCompanies = cateringCompanyRepository.findAll();

        return allCompanies.stream()
                .filter(company -> company.getLocation().equalsIgnoreCase(location)
                        && isAvailable(company.getId(), date, company.getMaxEventsPerDay()))
                .toList();
    }

    // Check if a company can still accept reservations on a specific date
    private boolean isAvailable(Long companyId, LocalDate date, int maxEvents) {
        List<Reservation> reservations = reservationRepository.findByCateringCompanyIdAndDateAndStatusIn(
                companyId, date, Arrays.asList("pending", "confirmed")
        ); //TODO: change this to a count query
        return reservations.size() < maxEvents;
    }

    // Create a reservation
    public Reservation reserveCatering(Long companyId, LocalDate date, String location) {
        if(date.isAfter(LocalDate.now()) || date.isAfter(LocalDate.now().plusYears(2))){
            throw new RuntimeException("Photographer booking date: "+date+ " is after or 2years before today: " +LocalDate.now());
        }
        Optional<CateringCompany> optionalCompany = cateringCompanyRepository.findById(companyId);

        if (optionalCompany.isEmpty()) {
            throw new RuntimeException("Catering Company with ID " + companyId + " not found.");
        }

        CateringCompany company = optionalCompany.get();

        if (!isAvailable(companyId, date, company.getMaxEventsPerDay())) {
            throw new RuntimeException("Catering company " + company.getName() + " is fully booked for " + date + ".");
        }

        Reservation reservation = new Reservation(
                companyId,
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

    // Confirm a reservation
    public void confirmReservation(Long reservationId) {
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

    // Cancel a reservation
    public void cancelReservation(Long reservationId) {
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

    // Fetch a catering company by ID
    public CateringCompany getCateringCompanyById(Long companyId) {
        return cateringCompanyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Catering Company with ID " + companyId + " not found."));
    }

    // List all distinct company locations
    public List<String> getAllDistinctLocations() {
        return cateringCompanyRepository.findDistinctLocationsOfAvailableCompanies();
    }
}
