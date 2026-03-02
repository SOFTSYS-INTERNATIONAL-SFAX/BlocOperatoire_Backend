package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.SurveillanceSSPI;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SurveillanceSSPIRepository
        extends JpaRepository<SurveillanceSSPI, UUID> {

    Page<SurveillanceSSPI> findBySspiSspiId(UUID sspiId, Pageable pageable);
}