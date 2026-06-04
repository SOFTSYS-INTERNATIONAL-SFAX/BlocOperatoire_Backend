package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.ConsultationPreAnesthesique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConsultationPreAnesthesiqueRepository
        extends JpaRepository<ConsultationPreAnesthesique, UUID> {

    @Override
    @EntityGraph(attributePaths = {"patient", "consentement", "intervention", "anesthesiste", "validatedBy"})
    Page<ConsultationPreAnesthesique> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"patient", "consentement", "intervention", "anesthesiste", "validatedBy"})
    Optional<ConsultationPreAnesthesique> findById(UUID consultationId);

    @EntityGraph(attributePaths = {"patient", "consentement", "intervention", "anesthesiste", "validatedBy"})
    Page<ConsultationPreAnesthesique> findByPatient_PatientId(UUID patientId, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "consentement", "intervention", "anesthesiste", "validatedBy"})
    Page<ConsultationPreAnesthesique> findByIntervention_InterventionId(UUID interventionId, Pageable pageable);

    @EntityGraph(attributePaths = {"patient", "consentement", "intervention", "anesthesiste", "validatedBy"})
    Optional<ConsultationPreAnesthesique> findFirstByIntervention_InterventionIdOrderByUpdatedAtDesc(UUID interventionId);
}
