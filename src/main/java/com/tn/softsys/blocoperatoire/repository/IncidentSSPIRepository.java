package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.IncidentSSPI;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IncidentSSPIRepository
        extends JpaRepository<IncidentSSPI, UUID> {

    Page<IncidentSSPI> findBySspiSspiId(UUID sspiId, Pageable pageable);
}