package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.FHIRResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FHIRResourceRepository
        extends JpaRepository<FHIRResource, UUID> {

    Page<FHIRResource> findByPatientPatientId(UUID patientId, Pageable pageable);

    Page<FHIRResource> findByInterventionInterventionId(UUID interventionId, Pageable pageable);
}