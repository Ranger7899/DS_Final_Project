package com.wedding.catering.repository;

import com.wedding.catering.model.CateringCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CateringCompanyRepository extends JpaRepository<CateringCompany, Long> {
    // Extra queries if needed later
}
