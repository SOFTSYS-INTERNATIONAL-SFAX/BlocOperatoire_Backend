package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Consentement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsentementRepository extends JpaRepository<Consentement, UUID> {

    Page<Consentement> findByPatientPatientId(UUID patientId, Pageable pageable);

    Page<Consentement> findByInterventionInterventionId(UUID interventionId, Pageable pageable);

    boolean existsByPatientPatientIdAndInterventionInterventionIdAndType(
            UUID patientId,
            UUID interventionId,
            String type
    );
}