package com.wedding.catering.service;

import com.wedding.catering.model.CateringCompany;
import com.wedding.catering.model.FoodType;
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

    // Get available companies for a specific date and location
    public List<CateringCompany> getAvailableCompanies(String dateString, String location) {
        LocalDate date = LocalDate.parse(dateString);

        // Find all companies in the location
        List<CateringCompany> allCompanies = cateringCompanyRepository.findAll();

        return allCompanies.stream()
                .filter(company -> company.getLocation().equalsIgnoreCase(location) && isAvailable(company.getId(), date, company.getMaxEventsPerDay()))
                .toList();
    }

    private boolean isAvailable(Long companyId, LocalDate date, int maxEvents) {
        List<Reservation> reservations = reservationRepository.findByCateringCompanyIdAndDateAndStatusIn(
                companyId, date, Arrays.asList("pending", "confirmed")
        );
        return reservations.size() < maxEvents;
    }

    // Make a reservation
    public Reservation reserveCatering(Long companyId, LocalDate date, String location, List<FoodType> foodTypes) {
        Optional<CateringCompany> optionalCompany = cateringCompanyRepository.findById(companyId);

        if (optionalCompany.isEmpty()) {
            throw new RuntimeException("Catering Company with ID " + companyId + " not found.");
        }

        CateringCompany company = optionalCompany.get();

        // Check if company is available for this date
        if (!isAvailable(companyId, date, company.getMaxEventsPerDay())) {
            throw new RuntimeException("Catering company " + company.getName() + " is fully booked for " + date + ".");
        }

        // Create a new reservation
        Reservation reservation = new Reservation(
                companyId,
                date,
                location,
                "pending", // Start as pending
                foodTypes
        );

        return reservationRepository.save(reservation);
    }

    // Confirm a reservation
    public void confirmReservation(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("confirmed");
            reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Reservation with ID " + reservationId + " not found.");
        }
    }

    // Cancel a reservation
    public void cancelReservation(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setStatus("cancelled");
            reservationRepository.save(reservation);
        } else {
            throw new RuntimeException("Reservation with ID " + reservationId + " not found.");
        }
    }

    // Get catering company by ID (if needed by broker later)
    public CateringCompany getCompanyById(Long companyId) {
        return cateringCompanyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Catering Company with ID " + companyId + " not found."));
    }
}
