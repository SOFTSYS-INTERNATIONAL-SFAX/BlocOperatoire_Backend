package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Consentement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConsentementRepository extends JpaRepository<Consentement, UUID> {

    @Override
    @EntityGraph(attributePaths = {"patient", "intervention", "verifiedBy"})
    Page<Consentement> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"patient", "intervention", "verifiedBy"})
    Optional<Consentement> findById(UUID consentId);

    @EntityGraph(attributePaths = {"patient", "intervention", "verifiedBy"})
    Page<Consentement> findByPatientPatientId(UUID patientId, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "intervention", "verifiedBy"})
    Page<Consentement> findByInterventionInterventionId(UUID interventionId, Pageable pageable);

    boolean existsByPatientPatientIdAndInterventionInterventionIdAndType(
            UUID patientId,
            UUID interventionId,
            String type
    );
}
