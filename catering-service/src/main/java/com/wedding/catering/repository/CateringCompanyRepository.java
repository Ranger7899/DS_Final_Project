package com.wedding.catering.repository;

import com.wedding.catering.model.CateringCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CateringCompanyRepository extends JpaRepository<CateringCompany, Long> {

    // Find all distinct locations of available catering companies
    @Query("SELECT DISTINCT c.location FROM CateringCompany c")
    List<String> findDistinctLocationsOfAvailableCompanies();

    // You might also want a method to find by id, but JpaRepository already gives you: findById(Long id)
}
